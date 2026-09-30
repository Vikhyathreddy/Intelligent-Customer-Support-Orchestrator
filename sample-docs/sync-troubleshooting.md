# Sync Troubleshooting

## Files are not syncing
1. Check the sync icon in the menu bar or system tray. A red icon means sync is paused.
2. Click the icon and choose **Resume sync**.
3. Make sure the file is smaller than 5 GB; larger files are skipped.
4. File names cannot contain the characters `< > : " | ? *`.

## Sync is slow
Sync speed is limited to 10 MB/s by default. To remove the limit, open **Settings > Network** and set
**Upload bandwidth** to **Unlimited**.

## "Conflicted copy" files
When the same file is edited on two devices while one is offline, Acme keeps both versions. The second
one is renamed with the suffix `(conflicted copy)`. Review both versions and delete the one you don't need.

## Resetting the sync database
If sync is stuck, quit Acme, delete the folder `~/.acme/syncdb` (macOS/Linux) or
`%APPDATA%\Acme\syncdb` (Windows), then restart Acme. Your files are not deleted; Acme rescans them.
