package com.ndb.auction.schedule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.PresaleService;
import com.ndb.auction.service.StatService;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.web3.NDBCoinService;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduledTasks {

	@Autowired
	AuctionService auctionService;

	@Autowired
	BidService bidService;

	@Autowired
	StatService statService;

	@Autowired
	PresaleService presaleService;

	@Autowired
	NDBCoinService ndbCoinService;

	@Autowired
	UserService userService;

	@Autowired
	InternalBalanceService balanceService;

	@Autowired
	MailService mailService;

	private Auction startedRound;
	private Long startedCounter;

	private Auction readyRound;
	private Long readyCounter;

	private PreSale startedPresale;
	private Long startedPresaleCounter;

	private PreSale readyPresale;
	private Long readyPresaleCounter;

	private final AmazonS3 s3;
	private final static String bucketName = "nyyu-dev-backup";
	// check transaction
	private Map<String, BigInteger> pendingTransactions;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final String CREDENTIALS_FILE_PATH = "google/credentials.json";
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private static final String APPLICATION_NAME = "Nyyu";

	public ScheduledTasks(AmazonS3 s3) {
		this.readyCounter = 0L;
		this.startedCounter = 0L;
		this.startedRound = null;
		this.readyRound = null;

		this.startedPresale = null;
		this.startedPresaleCounter = 0l;
		this.readyPresale = null;
		this.readyPresaleCounter = 0l;

		pendingTransactions = new HashMap<>();
		this.s3 = s3;
	}

	public void checkAllRounds() {
		Long currentTime = System.currentTimeMillis();
		
		// check Auctions
		List<Auction> auctions = auctionService.getAuctionByStatus(Auction.COUNTDOWN);
		if(auctions.size() != 0) {
			Auction auction = auctions.get(0);
			if(auction.getStartedAt() > currentTime) {
				// start count down
				setNewCountdown(auction);
				System.out.println(String.format("Auction Round %d is in countdown.", auction.getRound()));
				return;
			} else if (auction.getStartedAt() < currentTime && auction.getEndedAt() > currentTime) {
				// start round
				setStartRound(auction);
				auctionService.startAuction(auction.getId());
				System.out.println(String.format("Auction Round %d has been started.", auction.getRound()));
				return;
			} else {
				auctionService.endAuction(auction.getId());
				return;
			}
		}
		
		auctions = auctionService.getAuctionByStatus(Auction.STARTED);
		if(auctions.size() != 0) {
			Auction auction = auctions.get(0);
			if(auction.getStartedAt() < currentTime && auction.getEndedAt() > currentTime) {
				// start round
				setStartRound(auction);
				System.out.println(String.format("Auction Round %d has been started.", auction.getRound()));
				return;
			} else {
				auctionService.endAuction(auction.getId());
				return;
			}
		}

		List<PreSale> presales = presaleService.getPresaleByStatus(PreSale.COUNTDOWN);
		if(presales.size() != 0) {
			PreSale presale = presales.get(0);
			if(presale.getStartedAt() > currentTime) {
				setPresaleCountdown(presale);
				System.out.println(String.format("PreSale Round %d is in countdown.", presale.getId()));
				return;
			} else if (presale.getStartedAt() < currentTime && presale.getEndedAt() > currentTime) {
				setPresaleStart(presale);
				presaleService.startPresale(presale.getId());
				System.out.println(String.format("PreSale Round %d has been started.", presale.getId()));
				return;
			} else {
				presaleService.closePresale(presale.getId());
				return;
			}
		}

		presales = presaleService.getPresaleByStatus(PreSale.STARTED);
		if(presales.size() != 0) {
			PreSale presale = presales.get(0);
			if(presale.getStartedAt() < currentTime && presale.getEndedAt() > currentTime) {
				setPresaleStart(presale);
				System.out.println(String.format("PreSale Round %d has been started.", presale.getRound()));
				return;
			} else {
				presaleService.closePresale(presale.getId());
				return;
			}
		}
	}

	public Integer setNewCountdown(Auction auction) {

		if (this.readyRound != null) {
			return -1;
		}

		this.readyRound = auction;
		this.readyCounter = auction.getStartedAt() - System.currentTimeMillis();
		// convert into Seconds!!
		this.readyCounter /= 1000;

		return 1;
	}

	public void setStartRound(Auction auction) {
		if (this.startedRound != null) {
			return;
		}
		this.startedRound = auction;
		this.startedCounter = auction.getEndedAt() - System.currentTimeMillis();
		this.startedCounter /= 1000;
	}

	public void setPresaleCountdown(PreSale presale) {
		this.readyPresale = presale;
		this.readyPresaleCounter = presale.getStartedAt() - System.currentTimeMillis();
		this.readyPresaleCounter /= 1000;
	}

	public void setPresaleStart(PreSale presale) {
		this.startedPresale = presale;
		this.startedPresaleCounter = presale.getEndedAt() - System.currentTimeMillis();
		this.startedPresaleCounter /= 1000;
	}

	@Scheduled(fixedRate = 1000)
	public void AuctionCounter() {

		// count down ( ready round )
		if (readyRound != null && readyCounter > 0L) {
			readyCounter--;
			if (readyCounter <= 0) {
				// ended count down ! trigger to start this round!!

				startedRound = readyRound;
				startedCounter = (readyRound.getEndedAt() - readyRound.getStartedAt()) / 1000;

				int id = readyRound.getId();
				auctionService.startAuction(id);
				readyRound = null;
			}
		}

		// check current started round
		if (startedRound != null && startedCounter > 0L) {
			startedCounter--;
			if (startedCounter <= 0) {
				// end round!
				auctionService.endAuction(startedRound.getId());
				
				// bid processing
				// ********* checking delayed more 1s ************
				bidService.closeBid(startedRound.getId());
				statService.updateRoundCache(startedRound.getId());
				startedRound = null;
			}
		}
	
		if (readyPresaleCounter > 0L && readyPresale != null) {
			readyPresaleCounter--;
			if(readyPresaleCounter == 0L) {
				startedPresaleCounter = (readyPresale.getEndedAt() - readyPresale.getStartedAt()) / 1000;
				presaleService.startPresale(readyPresale.getId());
				startedPresale = readyPresale;
				readyPresale = null;
			}
		}

		if (startedPresale != null && startedPresaleCounter > 0L) {
			startedPresaleCounter--;
			if(startedPresaleCounter == 0) {
				// end
				presaleService.closePresale(startedPresale.getId());
				startedPresale = null;
				startedPresale = null;
			}
		}
	}

	// add pending list
	public void addPendingTxn(String hash, BigInteger blockNum) {
		if(pendingTransactions.containsKey(hash)) 
			return;
		pendingTransactions.put(hash, blockNum);
	}

	@Scheduled(fixedRate = 1000 * 120)
	public void checkConfirmation() {
		Set<String> hashSet = this.pendingTransactions.keySet();
		for (String hash : hashSet) {
			BigInteger target = this.pendingTransactions.get(hash);
			if(ndbCoinService.checkConfirmation(target)) {
				// set success
				System.out.println("SUCCESS: " + hash);
				// withdrawService.updateStatus(hash);
				pendingTransactions.remove(hash);
			}
		}
	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream();
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
			.setPort(8000)
			.build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

	private void compressTarGzip(Path outputFile, Path... inputFiles) throws IOException {
		try (OutputStream outputStream = Files.newOutputStream(outputFile);
			GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(outputStream);
			TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut)) {
	
			for (Path inputFile : inputFiles) {
				TarArchiveEntry entry = new TarArchiveEntry(inputFile.toFile());
				tarOut.putArchiveEntry(entry);
				Files.copy(inputFile, tarOut);
				tarOut.closeArchiveEntry();
			}
	
			tarOut.finish();
		}
	}

	@Scheduled(fixedRate = 1000 * 60 * 60)
	public void backupTables() throws IOException, GeneralSecurityException, MessagingException {
		// get ready for datetime
		log.info("Started backup..");

		var dateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		var hour = LocalDateTime.now().getHourOfDay();
		var userFileName = String.format("user-%s-%d.csv", dateTime, hour);
		var balanceFileName = String.format("balance-%s-%d.csv", dateTime, hour);
		var tarName = String.format("db-%s-%d.tar.gz", dateTime, hour);

		// loading user and balances from database & save to local file
		var users = userService.getAllUsers();
		var superAdmins = new ArrayList<User>();

		// filt writer
		var userOut = new FileWriter(userFileName);
		var balanceOut = new FileWriter(balanceFileName);
		
		var userPrinter = new CSVPrinter(userOut, CSVFormat.EXCEL);
		var balancePrinter = new CSVPrinter(balanceOut, CSVFormat.EXCEL); 
		try {
			userPrinter.printRecord("ID", "EMAIL");
			balancePrinter.printRecord("USER_ID", "FREE", "HOLD", "TOKEN");

			for (var user : users) {
				// check super admin
				if(user.getRole().contains("ROLE_SUPER")) {
					superAdmins.add(user);
				}

				userPrinter.printRecord(user.getId(), user.getEmail());
				var balances = balanceService.getInternalBalances(user.getId());
				for (var balance : balances) {
					balancePrinter.printRecord(user.getId(), balance.getFree(), balance.getHold(), balance.getTokenSymbol());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userPrinter.close();
			balancePrinter.close();
		}

		// // upload into google drive
		// final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		// Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
		// 	.setApplicationName(APPLICATION_NAME)
		// 	.build();
		
		// File userFileMetadata = new File();
		// userFileMetadata.setName(userFileName);

		// FileContent userMediaContent = new FileContent("text/csv", userFilePath);
		// File userFile = service.files().create(userFileMetadata, userMediaContent)
		// 	.setFields("id")
		// 	.execute();
		// // log.info(userFile.getId());
		// System.out.println(userFile.getId());
		
		// File balanceFileMetadata = new File();
		// balanceFileMetadata.setName(balanceFileName);
		
		java.io.File userFilePath = new java.io.File(userFileName);
		java.io.File balanceFilePath = new java.io.File(balanceFileName);
		// FileContent balanceMediaContent = new FileContent("text/csv", balanceFilePath);
		// File balanceFile = service.files().create(balanceFileMetadata, balanceMediaContent)
		// 	.setFields("id")
		// 	.execute();
		// // log.info(balanceFile.getId());
		// System.out.println(balanceFile.getId());

		// upload into S3 bucket
		log.info("Saved on local, getting ready for uploading....");

		var tarOut = Paths.get(tarName);
		compressTarGzip(tarOut, Paths.get(userFileName), Paths.get(balanceFileName));
		
		var tar = new java.io.File(tarName);
		var inputStream = new FileInputStream(tar);

		var metadata = new ObjectMetadata();
		metadata.setContentLength(tar.length());

		try {
			s3.putObject(bucketName, tarName, inputStream, metadata);
			
			// sending email
			mailService.sendBackupEmail(superAdmins, userFileName, balanceFileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userFilePath.delete();
			balanceFilePath.delete();
			tar.delete();
		}

		// delete local files
		log.info("Uploaded");
	}

}