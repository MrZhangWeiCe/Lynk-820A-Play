# LynkPlay — 领克 01 全球版 820A 适配

本仓库是基于 DiPlay 0.2.13 的车型适配分支，目标为 **领克 01 全球版、820A 车机、Android 9（API 28）**。应用及 iPhone CarPlay 接收端名称为 `LynkPlay`，CarPlay 内返回车机的按钮名称为 `Lynk`，图标为领克标识。

已实车确认：能够连接 CarPlay；关闭手机蓝牙后，再播放一次车机原生音乐，可正常播放 CarPlay 音乐。开启原生蓝牙连接时存在反复暂停问题。最新代码增加定向蓝牙隔离与播放器音频焦点申请，**这两项仍需实车复测，不能视为已经彻底解决**。其他领克车型、国内版及其他固件不在已验证范围内。

- [车型、修改说明、排查记录与测试步骤](docs/LYNK-820A-ADAPTATION.md)
- [本仓库发布与 APK](https://github.com/MrZhangWeiCe/Lynk-820A-Play/releases)
- [1.0.0-820A 发布说明（release 构建，沿用测试签名）](docs/RELEASE-NOTES-LYNKPLAY-1.0.0-820A.md)

保留原应用 ID `com.shihab.diplay`，属于替换安装，不可与相同应用 ID 的 DiPlay 同时安装。修改接收端名称不保证 iPhone 将其识别为另一辆独立车辆；身份与配对隔离尚未实车验证。

以下保留上游说明、来源及许可。上游 BYD 支持范围不代表本分支已验证的领克兼容性。

## 上游 DiPlay 说明

**CarPlay for compatible BYD Android head units.** Wired and wireless, with the familiar DiAuto interface. Independent app: `com.shihab.diplay`.

> **BYD support scope:** These projects focus on BYD cars. They may work on other brands, but other brands are unsupported and there are no plans to add support or fix brand-specific incompatibilities.

[Download & website](https://shihabal3amri.github.io/DiPlay/) · [Release](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.13) · [Report a problem](https://github.com/shihabal3amri/DiPlay/issues/new/choose)

![DiPlay home](site/assets/home.png)

## 0.2.13 — public preview

Install on the **car**, not the iPhone. No jailbreak, dongle, Mac, account or authentication server is required for use. Core CarPlay does not require ADB; optional dashboard, battery, wheel-speed and parked-video features do. Your head unit must permit APK installation. The APK supports Android 9+ (API 28); wireless supports Wi-Fi Direct, the car’s existing hotspot or Existing Wi-Fi / Same LAN. Android 9 Wi-Fi Direct uses a firmware-dependent legacy path with generated group credentials and unverified requested frequency; see [Android 9 Wi-Fi Direct](docs/ANDROID9_WIFI_DIRECT.md). Android 10+ verifies its negotiated group frequency.

- Wired USB and wireless CarPlay with local authentication.
- BYD HUD navigation with arrows, distance and street names on verified firmware.
- Car hotspot support, improved audio buffering and saved receive diagnostics.
- Automatic address discovery, fixed-channel Wi-Fi fallbacks and successful-configuration memory.
- Icon/text size, resolution and frame rate; applying a display change reconnects CarPlay.
- Local diagnostic export. Reports are sent only if you choose to share them.
- Separate installation alongside DiAuto. Run one projection app at a time.

This is **not an Apple-certified product**. The APK bundles an experimental accessory identity recovered from public Carlinkit firmware, not a newly provisioned MFi identity for DiPlay. A bundled private key is extractable. Acceptance after future iOS updates, reliability across head units and suitability of that identity for general distribution are unresolved. This release invites community testing; it is not a guarantee of universal compatibility.

Earlier releases were tested on the development DiLink5.1 car: live windshield guidance and street names work, Car hotspot now starts CarPlay, and Wi-Fi Direct performance is substantially improved. Occasional audio cutouts remain and are deferred to a later update. The floating-map test build was installed on the development DiLink 5.1 car; feedback led to the pinch corrections in 0.2.9. Earlier wheel-speed and video contributions were tested on a BYD Tang with DiLink 5.0 and an iPhone 15 Pro on iOS 27; wheel-speed dead reckoning in tunnels remains unverified. Broader head-unit and iOS compatibility is not guaranteed. The HUD firmware scope and cleanup limits are documented in [BYD navigation](docs/BYD_NAVIGATION.md).

## What’s new in 0.2.13

- Android 9 Wi-Fi Direct, IPv4-first hotspot endpoints, safer Auto channel ordering and wireless startup without unused NSD/USB services.
- Guarded rendered-video handoff fallback, safe wireless-to-USB switching and checked, cancellable permission setup.
- Narrow USBMUX trailer recovery that preserves valid payload replies.
- Optional live dock/split-screen areas and square-canvas rotation, plus the selected-decoder capability check and default-off experimental side panel.
- DiLink 4 casting/calibration and live cluster picture controls; checked DiLink 3 projection entry with compensation.
- Recent turn-card retention across wireless replacement, a finer dashboard map choice, battery-protocol fallback and eligible wheel-service recovery.
- The observed Siri microphone timestamp correction and TCP_NODELAY touch events, with device-specific performance limits.
- **Off by default:** independent experimental DiLink 3 call keys/dashboard calls and AAC-LC buffered music. Read their firmware/audio/restoration limits before opting in. Eligible hotspot join repair is a separately confirmed Check/Apply/Restore action.
- Clearer settings, saved-menu behavior, car-button customization and day/night-aware waiting screens.
- Traditional Chinese (Taiwan), bringing both the app and release website to seven languages.

See [0.2.13 release notes](docs/RELEASE-NOTES-0.2.13.md) and [validation](docs/VALIDATION.md) for all reviewed contributions, hardware evidence and remaining physical tests. Higher resolution and large square canvases cost more decoder/GPU work. General stutter, calls/Siri, decoder, old-iOS startup and model-specific reports remain under investigation. [0.2.12 notes](docs/RELEASE-NOTES-0.2.12.md) remain available as historical guidance.

If a problem remains, reproduce it on **0.2.13**, then use **Settings → Diagnostics → Save diagnostic report**. Android 10+ normally saves to **Downloads/DiPlay**; Android 9 uses the document picker. If unavailable, use **View report** or **Share** from the confirmation, which identifies external/private fallback storage. Review the `.txt` and add it to a matching [existing issue](https://github.com/shihabal3amri/DiPlay/issues), or [create one](https://github.com/shihabal3amri/DiPlay/issues/new/choose). Include vehicle/head-unit model, exact firmware and Android/DiLink, phone/iOS, connection backend, relevant settings, steps and failure time. Reports are shared only when you choose; never post your hotspot password.

## Documentation

[Existing Wi-Fi / Same LAN](docs/EXISTING_WIFI.md) keeps the iPhone and head unit
on an external router. See the guide for setup, build requirements and the
BYD DiLink 4.0 / Android 10 clean-install validation result.

- [Install and connect](docs/INSTALL.md)
- [Compatibility and troubleshooting](docs/COMPATIBILITY.md)
- [Smooth wireless CarPlay](docs/SMOOTH_WIRELESS.md)
- [Privacy and diagnostic reports](docs/PRIVACY.md)
- [Build from source](docs/BUILD.md)
- [Validation](docs/VALIDATION.md)
- [Release notes](CHANGELOG.md)
- [Credits and licenses](docs/THIRD_PARTY_NOTICES.md)

The app and release website are available in English, Arabic, Russian, Ukrainian, Spanish, Simplified Chinese and Traditional Chinese (Taiwan). Traditional Chinese uses Taiwan wording; the app also recognizes Hong Kong/Macao and explicit Hant selections without claiming separate regional translations. Choose the app language in Settings; on Android 13+, it stays synchronized with Android’s per-app language setting.

## Source and credits

Based on [xcertplay](https://github.com/shilapi/xcertplay), GPL-3.0. The home/settings UI and website adapt [DiAuto](https://github.com/shihabal3amri/DiAuto), AGPL-3.0; that license is included in `docs/licenses`. Preserve those notices when distributing modifications. CarPlay and its icon belong to Apple Inc.; no Apple or BYD affiliation or endorsement is implied.

This repository starts with a clean public source snapshot. Local research, tester reports and release-signing secrets are excluded. The complete source corresponding to the APK is provided with every release; experimental runtime identity assets are described separately in the build instructions and notices.

## Local release packaging

The release APK intentionally contains the experimental accessory identity. The Git repository and source archive exclude all accessory and Android signing keys; tests generate synthetic identities at runtime. Source/CI builds omit runtime identity assets by default. Local release builds explicitly select an external asset directory. Publishing the APK makes its bundled identity extractable; building locally does not preserve that identity's confidentiality.
