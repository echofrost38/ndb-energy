<!doctype html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Welcome to Nyyu UAB</title>
</head>
<style>
    * {
        font-family: Montserrat;
    }
    body {
        padding: 20px 50px
    }
    .container {
        margin: 80px 10px 120px;
    }
    footer {
        text-align: center;
        color: #1E1E1E;
        font-size: 10px;
        line-height: 12px;
    }

    .text {
        font-style: normal;
        font-weight: normal;
        font-size: 16px;
        line-height: 20px;
    }

    .number {
        font-style: normal;
        font-weight: bold;
        font-size: 48px;
        line-height: 59px;
        color: #23C865;
        padding: 60px 0
    }


    @media screen and (max-width: 700px) {
        body {
            padding: 20px
        }

        .normal {
            padding: 10px;
        }

        .logo {
            text-align: center;
        }
    }
</style>

<body>
    <header class="logo">
        <svg width="108" height="57" viewBox="0 0 108 57" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
                d="M69.9004 44.2524H95.2596V18.9756H73.0612V12.6504H69.9004V44.2524ZM73.0612 22.1262H92.0747V41.0777H73.0612V22.1262Z"
                fill="#1E1E1E" />
            <path
                d="M66.7167 12.6504H63.5558V18.9756H41.3574V44.2524H66.7167V12.6504ZM63.5558 41.1019H44.5424V22.1262H63.5558V41.1019Z"
                fill="#1E1E1E" />
            <path d="M38.0985 19.0242H12.5703V44.2525H15.6829V22.2231H34.9859V44.2525H38.0985V19.0242Z"
                fill="#23C865" />
        </svg>
    </header>
    <div class="container">
        <div class="text">Welcome to signup NYYU Auction</div>
        <br />
        <div class="text">Your NYYU Auction password is:</div>
        <div class="number">${code}</div>
        <div>If this wasn’t you, please Ignore this message. </div>
    </div>
    <footer>
        <p>© 2021 Voltamond SA. All rights reserved.</p>
        <p>ndb.money +41 (275) 083 211</p>
    </footer>
</body>

</html>
