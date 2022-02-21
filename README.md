![app-icon](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

# AryKey \[ 🚧🧑‍🏭 Work in progress 🔧🚧 \]

Android application that prepares an hardware device via USB serial port with a specific password
generated deterministically based on three (3) inputs:
the `App` we want to `Unlock`, the `User ID` used for login (typically an email address)
and the `PIN` (6 numeric digits) we want to associate with previous inputs.

The hardware being used, during development, is a RPi Pico flashed with a custom firmware. It
implements a key derivation function (KDF) that derives deterministic passwords from the given
elements and a unique key stored in the ROM.

This app loads the 'hardware key' with the user inputs, and the KDF will derive a password for it.
The password is not transmitted back via USB to eliminate a possible attack vector, instead is typed
by emulating an external keyboard when the user clicks the physical button on the device.


### Architecture

* [MVVM](https://developer.android.com/jetpack/guide)
* [Kotlin](https://kotlinlang.org/)
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
* [Flow](https://kotlinlang.org/docs/flow.html)


### How to use during Dev

1. Install & launch the *AryKey app*
2. Plug in hardware device into the USB port of the phone (OR) Skip the connect screen if you don't
   have the hardware device yet
3. Click on the SnackBar action to jump to the Accessibility settings and enable it if you want the
   app to auto detect the current/last app in foreground
4. Type the `App` name if you haven't enabled Accessibility for this app yet
5. Type the respective `User ID` (typically an email address)
   (OR) If you click the dropdown icon it will ask permission to access Contacts so it can suggest
   your existing email accounts to autocomplete
6. Click `Unlock`
7. Type a 6 digits `PIN` (a different PIN will derive a different password)
8. Wait for a success or error response* from the communication with the hardware device
9. Done!

> At this point, by pressing the hardware button of the device it starts typing the password in the current input field with focus.

*As long the device receives the data in the correct format, it will always return a success code.
That means any entered PIN will work, so that we can eliminate another possible vector of attack.


### How to use everyday

1. Assuming you are in the login screen of a random app, _Linkedin_ app for example, just plug in
   the device into the USB port and the **AryKey app** will open automatically with the _Linkedin_
   app auto-filled.
2. If the first time choosing this app, type your `User ID` which in this case will be an email
   address. Following times will be auto-populated, with your last input for this app.
3. Click `Unlock`
4. Type your 6 digits `PIN`
5. Done! _(preparing the device)_

### Features

- [x] UX concept solely with bottom sheets
- [x] Auto launch the app when the hardware device (Raspberry Pi Pico) is plugged in
- [x] Auto detect current/last app in foreground with `AccessibilityService`
- [x] Obtain the list of installed apps in the phone, for user manual selection
- [x] Retrieve device email accounts given permission from user during runtime
- [x] Auto load image associated to given email address, either from Android Contacts
  or [Gravatar API](https://en.gravatar.com/site/implement/)
- [x] Certificate pinning of host `gravatar.com` to prevent connections through man-in-the-middle
  certificate authorities
- [x] Simple hostname check to help preventing man-in-the-middle attacks
- [x] Request PIN input with 6 digits from the user
- [x] Communication with hardware device (Raspberry Pi Pico) via Serial USB
- [x] When app is 'minimised' show notification with status and an action to help jumping back in
- [x] Lottie Animations
- [x] Snackbars with enabled swipe to dismiss
- [x] Dark-Theme support


### TODO

- [ ] Navigate to right fragment when hardware device is plugged in
- [ ] Replace hardcoded success event
- [ ] Persist encrypted in SharedPref a map of `App`<>`User ID`
- [ ] Hash PIN+App+Email before sending it over USB serial
- [ ] Auto-detect current URL when used with a known browser app
- [ ] Parse app domain from package name and use that instead to ensure cross-compatibility
- [ ] Migrating LiveData to StateFlow
- [ ] Increase test code coverage
- [ ] [Migrate to Compose UI](https://developer.android.com/jetpack/compose/interop)
- [ ] [Navigating in Compose](https://commonsware.com/blog/2022/01/22/navigating-compose-criteria.html)
- [ ] Add LICENSE
- [ ] More TBD


### Known Bugs

* Padding bottom of the Snackbar is incorrect (this was working but I broke it somehow =/)
* Dropdown popup background of the `App` input is broken. Style with rounded corners not being
  applied and some overlay is occurring.
* Dropdown popup of the `User ID` input have the wrong anchor the first time and its conflicting
  with the keyboard events.
  
Note: Some view & layouts are not fully optimised because they will be discarded soon when migrating to compose UI


### Screenshots

<img src="https://github.com/ryanamaral/arykey-android/raw/dev/screenshots/main.png" width="260"> <img src="https://github.com/ryanamaral/arykey-android/raw/dev/screenshots/id.png" width="260"> <img src="https://github.com/ryanamaral/arykey-android/raw/dev/screenshots/auth.png" width="260">
