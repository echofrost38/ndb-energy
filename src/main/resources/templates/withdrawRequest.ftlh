<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Montserrat|Arial|Raleway"
      rel="stylesheet"
    />
    <title>Nyyu</title>
  </head>
  <body>
    <div
      style="
        width: 500px;
        height: auto;
        padding-left: 30px;
        font-family: 'Montserrat', Arial, Raleway;
        overflow: hidden;
        "
    >
    <div
    style="
    padding-top: 10px
    "
    >
        <img src="https://nyyu-resources.s3.eu-west-1.amazonaws.com/nyyu.png" width="51" height="42" />    
    </div>
    
      <div
        style="
          font-weight: 500;
          font-size: 16px;
          line-height: 20px;
          color: black;
          padding-top: 40px;
          padding-bottom: 20px;
        "
      >
        A Withdrawal Request via ${withdrawType} has been logged.
      </div>
      <div
        style="
          font-weight: 500;
          font-size: 16px;
          line-height: 20px;
          color: black;
          padding-top: 40px;
          padding-bottom: 20px;
        "
      >
        User Details
      </div>
      <div
        style="
          font-weight: 500;
          font-size: 16px;
          color: black;
          line-height: 22px;
        "
      >
        Prefix-Username: ${avatarName}
        Email Address: ${email}
        Full Name: ${fullName}
        Address: ${address}
        Country: ${country}

        Current Balances: ${balance}
        Show only available balances


        Payment Details
        Requested Amount: ${requestAmount}
        Requested Payment Method: ${withdrawType}
        Requested Payment Currency: ${currency}     

        ${typeMessage} for transfer: ${destination}
        ${bank}         
      </div>
      
      <div
        style="
          font-weight: 500;
          font-size: 16px;
          color: black;
          line-height: 22px;
          padding-bottom: 10px;
          text-align: justify;
        "
      >
      Don’t recognize this activity? Please
      <a style="color: #63AAEB" href="https://www.nyyu.io/app/forgot-password/">reset your password</a>
      and contact 
      <a style="color: #63AAEB" href="https://help.nyyu.io/hc/en-gb/requests/new">customer support </a>
      immediately.
      </div>

      <div
        style="
          font-size: 10px;
          line-height: 14px;
          color: black;
          padding-top: 36px;
          padding-bottom: 10px;
          text-align: justify;

        ">
          <strong>Kindly note:</strong> Please be aware of phishing sites and always make sure you are visiting the official nyyu.io website when entering sensitive data.
          </div>

          <div
          style="
            font-size: 10px;
            line-height: 14px;
            color: black;
            padding-bottom: 10px;
            text-align: left;
          ">
          © 2022 NYYU. All rights reserved.
        </div>
      </div>
    </div>
  </body>
</html>
