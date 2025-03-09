# WSO2 API Certificate Tracker

This script automates and tracks the usage of MTLS certificates across multiple APIs in a WSO2 environment. With numerous APIs secured by over 100 certificates, where each certificate may be used by multiple APIs, it’s essential to keep track of which certificates are associated with which APIs. This becomes particularly important during certificate renewals. So with this script you can get list of apis for particular CN.

The primary goal of this project is to streamline the process of identifying which APIs are using a specific certificate (based on its Common Name, CN) and which certificates are being utilized by a particular API.

## Requirements

- Java 8 or higher
- WSO2 API Manager
- API credentials for accessing the WSO2 Publisher APIs
## Project Overview

This project automates the following tasks:

- Retrieve all APIs associated with a particular certificate CN.
- Track certificates and APIs to simplify the management of certificate renewals.

## Building from the source
If you want to build WSO2-api-cert-checke from the source code:
1. Clone or download the source code from the repository:
git clone https://github.com/kajal1410/WSO2-api-cert-checker.git
2. Switch to the main branch:
``git checkout main``
3. Navigate to the Certificate_Manager directory and run the following Maven command.
``mvn clean install``

