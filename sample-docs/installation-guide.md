# Installation Guide

## System requirements
- 64-bit Windows 10 or later, macOS 13 or later, or Ubuntu 22.04 or later
- 8 GB RAM minimum, 16 GB recommended
- 2 GB of free disk space

## Installing on Windows
1. Download `acme-setup.exe` from the Downloads page of your account dashboard.
2. Right-click the installer and choose **Run as administrator**.
3. Follow the wizard. The default install location is `C:\Program Files\Acme`.
4. Sign in with your account email when the app launches.

## Installing on macOS
1. Download `Acme.dmg` from the Downloads page.
2. Drag **Acme** into the Applications folder.
3. On first launch, macOS may warn that the app was downloaded from the internet. Click **Open**.

## Installing on Linux
Run `sudo apt install ./acme_amd64.deb` from the folder you downloaded the package to.

## Error 1603 during Windows installation
Error 1603 means the installer could not write to the install directory. Close any running copy of Acme,
make sure you ran the installer as administrator, and temporarily disable third-party antivirus software.
If the error persists, delete `C:\Program Files\Acme` and run the installer again.
