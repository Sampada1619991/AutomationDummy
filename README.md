# AutomationDummy

> Selenium automation examples for browser-based testing.

## Project overview

AutomationDummy contains small Selenium examples for experimenting with browser automation, locating elements, and validating scripted interactions. The examples are intentionally lightweight and should be treated as learning and test fixtures rather than production automation.

## Features

- Selenium examples for Chrome and Firefox.
- Direct Java execution without a Maven or Gradle project.
- Environment-variable guidance for credentials.
- Troubleshooting notes and known limitations.

## Repository structure

```text
AutomationDummy/
├── README.md
├── GoogleSearch.java
└── GoogleSearchFirefox.java
```

File names may vary if the examples are reorganized; inspect the repository before compiling.

## Prerequisites

- Java Development Kit (JDK), with a version supported by the installed Selenium release.
- Google Chrome for the Chrome example and Mozilla Firefox for the Firefox example.
- Matching ChromeDriver and GeckoDriver binaries, or a Selenium Manager-capable Selenium setup.
- Internet access to download Selenium dependencies and launch the browser targets.

The repository currently has no Maven `pom.xml` or Gradle build configuration. Selenium dependencies therefore need to be supplied manually through downloaded JAR files and their transitive dependencies, or by adding a build configuration locally.

## Selenium dependency setup

Download a compatible Selenium Java distribution from the official Selenium releases or Maven Central, then include the Selenium JARs and required dependency JARs on the Java classpath. Keep browser drivers compatible with the installed browsers and ensure the driver executables are on `PATH`, or provide their locations through Selenium configuration.

Do not commit downloaded dependencies, driver binaries, or credentials to this repository.

## Running the examples

Compile and run the Chrome example with a classpath containing Selenium and its dependencies:

```bash
javac -cp "lib/*" GoogleSearch.java
java -cp ".:lib/*" GoogleSearch
```

On Windows, use `;` instead of `:` in the runtime classpath:

```bat
java -cp ".;lib/*" GoogleSearch
```

Run the Firefox example similarly:

```bash
javac -cp "lib/*" GoogleSearchFirefox.java
java -cp ".:lib/*" GoogleSearchFirefox
```

The examples open their respective browsers and perform the scripted Google sign-in/search interaction. Close the browser and terminate the process if an example does not finish cleanly.

## Credentials and security

Credentials must be supplied through environment variables or another local secret-management mechanism; do not hard-code them in Java source. For example:

```bash
export GOOGLE_USERNAME="your-test-account"
export GOOGLE_PASSWORD="your-test-password"
```

Never use a personal or production account for an automated sign-in test. The Google login flow may block automation, trigger additional verification, or violate service terms. Any credentials previously exposed in source history should be rotated immediately.

## Expected behavior and known limitations

- A browser window is launched and controlled through Selenium.
- Results depend on network connectivity, browser versions, driver compatibility, and Google account state.
- Google sign-in is not a stable automation target: CAPTCHA, MFA, consent screens, bot detection, and UI changes can interrupt the flow.
- The Google sign-in selectors in the examples are outdated and may no longer identify the intended controls.
- Headless execution, retries, explicit waits, assertions, and reliable cleanup are not guaranteed by the current examples.
- Because no Maven or Gradle configuration exists, setup is manual and classpath errors are possible.

## Troubleshooting

### Driver is not found

Install the matching driver, place it on `PATH`, or configure its absolute path. Verify the browser and driver versions are compatible.

### Class or dependency errors

Confirm that every Selenium and transitive dependency JAR is present in `lib/` and that the classpath syntax matches the operating system.

### Element cannot be located

The Google page may have changed, or a sign-in step may have redirected to a consent, CAPTCHA, or verification page. Inspect the page manually and replace outdated selectors with stable locators and explicit waits.

### Login is blocked

Use a dedicated test account, review Google security notifications, and expect that automated sign-in may be rejected. Do not attempt to bypass CAPTCHA or other security controls.

## Recommended improvements

- Add Maven or Gradle dependency management.
- Replace hard-coded or outdated selectors with maintained page objects.
- Use explicit waits, assertions, structured logging, and `try/finally` browser cleanup.
- Parameterize browser, base URL, and test data.
- Add CI-safe tests that do not require real credentials or third-party login.
- Store secrets in CI secret storage and add a clear license.

## Contributing

Keep changes focused, document setup changes, and avoid committing secrets, browser binaries, generated artifacts, or local configuration. Validate documentation and, where practical, run examples with a dedicated test account before opening a pull request.

## License and usage disclaimer

No explicit license file is currently included. Copyright and reuse permissions should not be assumed until a license is added. This project is provided for educational and testing purposes; users are responsible for complying with Selenium, browser, Google, and organizational policies.
