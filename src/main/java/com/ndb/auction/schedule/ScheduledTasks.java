package com.ndb.auction.schedule;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ndb.auction.dao.oracle.user.UserDetailDao;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.AuctionService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.PresaleService;
import com.ndb.auction.service.ShuftiService;
import com.ndb.auction.service.StatService;
import com.ndb.auction.service.TokenAssetService;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.ndb.auction.web3.NDBCoinService;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    TokenAssetService tokenAssetService;

    @Autowired
    ShuftiService shuftiService;

    @Autowired
    UserDetailDao userDetailDao;

    @Autowired
    ThirdAPIUtils apiUtils;

    @Getter
	private Auction startedRound;
    @Getter
	private Long startedCounter;

    @Getter
	private Auction readyRound;
    @Getter
	private Long readyCounter;

    @Getter
	private PreSale startedPresale;
    @Getter
	private Long startedPresaleCounter;

    @Getter
	private PreSale readyPresale;
    @Getter
	private Long readyPresaleCounter;

    private final AmazonS3 s3;

    @Value("${backup.s3.bucket}")
	private String bucketName;
    // check transaction
    private Map<String, BigInteger> pendingTransactions;

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
        if (auctions.size() != 0) {
            Auction auction = auctions.get(0);
            if (auction.getStartedAt() > currentTime) {
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
                bidService.closeBid(startedRound.getId());
                statService.updateRoundCache(startedRound.getId());
                startedRound = null;
                return;
            }
        }

        auctions = auctionService.getAuctionByStatus(Auction.STARTED);
        if (auctions.size() != 0) {
            Auction auction = auctions.get(0);
            if (auction.getStartedAt() < currentTime && auction.getEndedAt() > currentTime) {
                // start round
                setStartRound(auction);
                System.out.println(String.format("Auction Round %d has been started.", auction.getRound()));
                return;
            } else {
                auctionService.endAuction(auction.getId());
                bidService.closeBid(startedRound.getId());
                statService.updateRoundCache(startedRound.getId());
                startedRound = null;
                return;
            }
        }

        List<PreSale> presales = presaleService.getPresaleByStatus(PreSale.COUNTDOWN);
        if (presales.size() != 0) {
            PreSale presale = presales.get(0);
            if (presale.getStartedAt() > currentTime) {
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
        if (presales.size() != 0) {
            PreSale presale = presales.get(0);
            if (presale.getStartedAt() < currentTime && presale.getEndedAt() > currentTime) {
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
		log.info("Started Presale Counter: {}", this.startedPresaleCounter);
    }

    private boolean auctionCounter;

    @Scheduled(fixedRate = 1000)
    public void AuctionCounter() {
        if (!auctionCounter) {
            auctionCounter = true;
            checkAllRounds();
        }
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
            if (readyPresaleCounter == 0L) {
                startedPresaleCounter = (readyPresale.getEndedAt() - readyPresale.getStartedAt()) / 1000;
                presaleService.startPresale(readyPresale.getId());
                startedPresale = readyPresale;
                readyPresale = null;
            }
        }

        if (startedPresale != null && startedPresaleCounter > 0L) {
            startedPresaleCounter--;
            if (startedPresaleCounter == 0) {
                // end
                presaleService.closePresale(startedPresale.getId());
                startedPresale = null;
            }
        }
    }

    // add pending list
    public void addPendingTxn(String hash, BigInteger blockNum) {
        if (pendingTransactions.containsKey(hash))
            return;
        pendingTransactions.put(hash, blockNum);
    }

    public void forceToClosePresale(int presaleId) {
        presaleService.closePresale(presaleId);
        startedPresale = null;
        startedPresaleCounter = 0L;
    }

    @Scheduled(fixedRate = 1000 * 120)
    public void checkConfirmation() {
        Set<String> hashSet = this.pendingTransactions.keySet();
        for (String hash : hashSet) {
            BigInteger target = this.pendingTransactions.get(hash);
            if (ndbCoinService.checkConfirmation(target)) {
                // set success
                System.out.println("SUCCESS: " + hash);
                // withdrawService.updateStatus(hash);
                pendingTransactions.remove(hash);
            }
        }
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
	public void backupTables() throws IOException {
		// get ready for datetime
		log.info("Started backup..");

		var dateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		var hour = LocalDateTime.now().getHourOfDay();
		var userFileName = String.format("user-%s-%d.csv", dateTime, hour);
		
		var tarName = String.format("db-%s-%d.tar.gz", dateTime, hour);

		// loading user and balances from database & save to local file
		var users = userService.getAllUsers();
		var superAdmins = new ArrayList<User>();
		var tokens = tokenAssetService.getAllTokenAssets(null);

		var headerList = new ArrayList<String>();
		headerList.add("ID");
		headerList.add("EMAIL");
		headerList.add("PREFIX");
		headerList.add("NAME");
		headerList.add("KYC STATUS");
		headerList.add("FIRSTNAME");
		headerList.add("SURNAME");
		headerList.add("ADDRESS");
		headerList.add("COUNTRY");
		headerList.add("ZIP");
		
		var prices = new ArrayList<Double>();
		for (var token : tokens) {
			headerList.add(token.getTokenSymbol() + "_FREE");
			headerList.add(token.getTokenSymbol() + "_HOLD");
			var price = apiUtils.getCryptoPriceBySymbol(token.getTokenSymbol());
			prices.add(price);
		}
		headerList.add("TOTAL_BALANCE");
		
		// filt writer
		var userOut = new FileWriter(userFileName);
		
		var userPrinter = new CSVPrinter(userOut, CSVFormat.EXCEL);
		try {
			userPrinter.printRecord(headerList.toArray());

			for (var user : users) {
				// check super admin
				if(user.getRole().contains("ROLE_SUPER")) {
					superAdmins.add(user);
				}
				var row = new ArrayList<Object>();
				row.add(user.getId());
				row.add(user.getEmail());
				row.add(user.getAvatar() == null ? "" : user.getAvatar().getPrefix());
				row.add(user.getAvatar() == null ? "" : user.getAvatar().getName());

				// check kyc
				boolean kycStatus = shuftiService.kycStatusCkeck(user.getId());
				row.add(kycStatus ? "PASSED" : "FAILED");

				// get user details
				var userDetail = userDetailDao.selectByUserId(user.getId());
				if(userDetail != null) {
					row.add(userDetail.getFirstName());
					row.add(userDetail.getLastName());
					var address = userDetail.getAddress();
					row.add(address);
					var addressArray = address.split("\\s*,\\s*");
					row.add(addressArray[addressArray.length - 1]);
					row.add("");
				} else {
					row.add("");
					row.add("");
					row.add("");
					row.add("");
					row.add("");
				}

				var totalBalance = 0.0; 
				var i = 0;
				for (var token : tokens) {
					var tempBalance = balanceService.getBalance(user.getId(), token.getTokenSymbol());
					var free = tempBalance == null ? 0.0 : tempBalance.getFree();
					var hold = tempBalance == null ? 0.0 : tempBalance.getHold();
					row.add(free); row.add(hold);
					totalBalance += (free + hold) * prices.get(i++);
				}
				row.add(totalBalance);
				userPrinter.printRecord(row);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userPrinter.close();
		}
		
		java.io.File userFilePath = new java.io.File(userFileName);

		// upload into S3 bucket
		log.info("Saved on local, getting ready for uploading....");

		var tarOut = Paths.get(tarName);
		compressTarGzip(tarOut, Paths.get(userFileName));
		
		var tar = new java.io.File(tarName);
		var inputStream = new FileInputStream(tar);

		var metadata = new ObjectMetadata();
		metadata.setContentLength(tar.length());

		try {
			s3.putObject(bucketName, tarName, inputStream, metadata);
			
			// sending email
			mailService.sendBackupEmail(superAdmins, userFileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userFilePath.delete();
			tar.delete();
		}

		// delete local files
		log.info("Uploaded");
	}
	 

}