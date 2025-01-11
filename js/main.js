const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

console.log("Script is run")
function randomDelay(min = 1500, max = 4000) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

async function moveMouseRandomly(page) {
    const mouse = page.mouse;
    const x = Math.floor(Math.random() * 1000);
    const y = Math.floor(Math.random() * 1000);
    await mouse.move(x, y, { steps: Math.floor(Math.random() * 20) + 10 });
}

async function userBehavior(page) {
    await new Promise(resolve => setTimeout(resolve, randomDelay()));
    await moveMouseRandomly(page);
}

async function typeWithDelay(page, selector, text) {
    await page.waitForSelector(selector);
    for (let i = 0; i < text.length; i++) {
        await page.type(selector, text[i]);
        await new Promise(resolve => setTimeout(resolve, randomDelay(100, 300)));
    }
}

async function acceptCookies(page) {
    try {
        await page.waitForSelector('#onetrust-accept-btn-handler', { visible: true, timeout: 10000 });
        await page.click('#onetrust-accept-btn-handler');
        console.log('Кнопка принятия cookies успешно нажата!');
        await userBehavior(page);
    } catch (error) {
        console.error('Ошибка при принятии cookies:', error);
    }
}

async function goToLoginPage(page) {
    await userBehavior(page);
    await page.waitForSelector('.css-12l1k7f', { visible: true });
    await page.click('.css-12l1k7f');
    console.log('Кнопка входа успешно нажата.');
    await page.waitForNavigation({ waitUntil: 'networkidle2' });
}

async function login(page) {
    await page.waitForSelector('#username', { visible: true });
    await typeWithDelay(page, '#username', 'Seva552.seva@gmail.com');
    console.log('Логин введён.');

    await page.waitForSelector('#password', { visible: true });
    await typeWithDelay(page, '#password', 'Sdfanc552@');
    console.log('Пароль введён.');

    await page.waitForSelector('button[data-testid="login-submit-button"]', { visible: true });
    await page.click('button[data-testid="login-submit-button"]');
    console.log('Кнопка "Zaloguj się" успешно нажата.');

    await page.waitForNavigation({ waitUntil: 'networkidle2' });
    console.log('Переход на новую страницу завершён.');
}

async function setupBrowser() {
    const browser = await puppeteer.launch({
        headless: false,
        args: [
            '--no-sandbox',
            '--disable-setuid-sandbox',
            '--disable-extensions',
            '--start-maximized',
            '--disable-infobars'
        ],
        defaultViewport: null,
    });

    return browser;
}

async function configurePage(page) {
    const userAgents = [
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36',
        'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36'
    ];
    const userAgent = userAgents[Math.floor(Math.random() * userAgents.length)];
    await page.setUserAgent(userAgent);

    await page.evaluateOnNewDocument(() => {
        Object.defineProperty(navigator, 'webdriver', { get: () => undefined });
    });

    const cookiesPath = path.resolve(__dirname, 'cookies.json');
    if (fs.existsSync(cookiesPath)) {
        const cookies = JSON.parse(fs.readFileSync(cookiesPath, 'utf-8'));
        await page.setCookie(...cookies);
    }
}

async function saveCookies(page) {
    try {
        const cookies = await page.cookies();
        const cookiesPath = path.resolve(__dirname, 'cookies.json');
        fs.writeFileSync(cookiesPath, JSON.stringify(cookies, null, 2), 'utf-8');
        console.log('Куки успешно сохранены!');
    } catch (error) {
        console.error('Ошибка при сохранении куки:', error);
    }
}

const mainServiceUrl = 'http://localhost:8081/api/apartments/phoneNumber'
const args = process.argv.slice(2);

(async () => {
    const browser = await setupBrowser({headless : true});
    const page = await browser.newPage();
    const link = args[0]
    try {
        await configurePage(page);

        await page.goto(link, { waitUntil: 'networkidle2' });

        await page.waitForSelector('#onetrust-accept-btn-handler', { visible: true, timeout: 2000 });
        await page.click('#onetrust-accept-btn-handler');
        console.log("Cookies accepted")

        await page.waitForSelector('button[data-cy="phone-number.show-full-number-button"]', { visible: true, timeout: 10000 });
        await page.click('button[data-cy="phone-number.show-full-number-button"]');
        console.log("Show number pressed")

        await page.waitForSelector('.css-1yb5pbx.e1fy45eu0 a[href^="tel:"]', { visible: true, timeout: 10000 });
        const phoneNumber = await page.$eval('.css-1yb5pbx.e1fy45eu0 a[href^="tel:"]', el => el.innerText);

        console.log('Номер телефона:', phoneNumber);
        console.log('Отправка номера для:', link);

        await fetch(mainServiceUrl, {
            method : 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ link: link, phoneNumber: phoneNumber })
        }).then(response => {
            if (response.ok) {
                console.log("Запрос с номером телефона успешно отправлен")
            }
        })


        // await acceptCookies(page);
        //
        // await goToLoginPage(page);
        //
        // await login(page);
        //
        // await saveCookies(page);

    } catch (error) {
        console.error('Ошибка автоматизации:', error);
    } finally {
        await browser.close();
    }
})();

// (async () => {
//     const browser = await puppeteer.launch({ headless: false });
//     const page = await browser.newPage()
//     try {
//         await configurePage(page);
//         // Navigate to the target URL
//         await page.goto('https://www.otodom.pl/pl/oferta/funkcjonalne-3-pok-do-remontu-ul-grzybowska-en-ID4tMq3.html', { waitUntil: 'networkidle2' });
//
//         // Wait for the button to appear
//         const buttonSelector = '.css-1bzy56a';
//         await page.waitForSelector(buttonSelector, { visible: true, timeout: 10000 });
//
//         // Click the button
//         await page.click(buttonSelector);
//
//         // Optionally, wait for the phone number to appear
//         const phoneNumberSelector = '.css-xxxxx'; // Replace with the actual selector of the phone number
//         await page.waitForSelector(phoneNumberSelector, { visible: true, timeout: 10000 });
//
//         // Extract and log the phone number
//         const phoneNumber = await page.$eval(phoneNumberSelector, el => el.textContent.trim());
//         console.log('Phone Number:', phoneNumber);
//
//     } catch (error) {
//         console.error('Error during automation:', error);
//     } finally {
//         await browser.close();
//     }
// })();

// async function collectOtodomNumber(url) {
//     const browser = await setupBrowser();
//     const page = await browser.newPage();
//
//     await configurePage(page)
//
//     await page.goto(url)
// }
