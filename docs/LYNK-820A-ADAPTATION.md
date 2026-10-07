# LynkPlay 820A 适配与排查记录

更新日期：2026-10-07（Asia/Shanghai）。基于 DiPlay 0.2.13。

## 适配车型与固件范围

目标车型：领克 01 全球版。车机版本：820A。Android 9，API 28。

实车日志中的关键识别字段：

```text
Build.HARDWARE = dcy11_a1
Build.ID = PQ3B.190801.002
Build.DISPLAY = PQ3B.190801.002 test-keys
Build.FINGERPRINT prefix = geely/dcy11_a1/dcy11:9/PQ3B.190801.002/
```

专用策略限定 Android 9，并匹配硬件、构建 ID/指纹或历史显示字段。不是所有 Android 9 或所有领克车机都启用。其他车型、国内版、不同固件、通话/Siri、方控完整行为仍需分别实车验证。

## 修改说明

- 应用名与 CarPlay 接收端广播名称改为 `LynkPlay`。
- CarPlay 内 OEM 返回按钮默认名称改为 `Lynk`，替换为领克图标；迁移旧 BYD/LynkPlay 默认标签，保留用户自定义标签。
- 补充 Android 9 媒体按键兼容，保留播放/暂停、上一曲、下一曲的 CarPlay 媒体 HID 转发。实车方控全项验收尚未完成。
- 恢复 CarPlay 请求车机界面时启动 Android HOME 的行为，不再强制留在投屏界面。
- 播放器遵循“音频焦点”设置；820A 无保存偏好时默认开启。辅助方控 MediaSession 不重复申请焦点。
- 仅在无线 CarPlay 已要求蓝牙交接且 Wi-Fi iAP2 通道认证就绪后，尝试断开当前 iPhone 的原生 A2DP Sink（11）和 Headset Client（16）连接。
- 蓝牙隔离不关闭总蓝牙，不删除配对，不修改设备连接优先级，不操作其他手机。仅会话内处理重连，每个通道最多尝试三次；会话结束解除监听。不承诺结束后主动恢复原生连接。
- 导出日志增加媒体 HID 发送、媒体回调来源、蓝牙音频状态、隔离请求结果及音频流拆除信息，不导出对应协议正文或设备名称/MAC。

蓝牙接口为 Android 9 隐藏 API，车机可能因权限、隐藏 API 限制或厂商实现拒绝请求。`disconnectAccepted=true` 仅表示请求被接受，不等于已经断开；需结合后续连接状态和实车播放判断。

接口参考：[AOSP Android 9 BluetoothA2dpSink](https://raw.githubusercontent.com/aosp-mirror/platform_frameworks_base/android-9.0.0_r1/core/java/android/bluetooth/BluetoothA2dpSink.java)、[BluetoothHeadsetClient](https://raw.githubusercontent.com/aosp-mirror/platform_frameworks_base/android-9.0.0_r1/core/java/android/bluetooth/BluetoothHeadsetClient.java)。

## 排查记录与证据边界

1. 已能连接，播放数秒/约一秒后暂停；手机与 CarPlay 屏幕两边操作均复现。
2. 最初怀疑音频焦点与 requestUI 跳转。关闭焦点后暂停仍发生；日志显示暂停早于返回车机按钮点击，否定 requestUI 是主要原因。返回桌面功能已恢复。
3. 16:35 会话确认专用策略生效，但仍反复暂停。Android 音频轨正常运行，iPhone 报告暂停后继续发送小静音包，并最终拆除音频流。不能据此证明具体是谁发送原生蓝牙暂停命令。
4. 16:48:49 新会话中蓝牙音频/电话通道均未连接，持续播放约 57 秒；16:49:46 两通道重新连接时暂停。16:49:49 再播放，约 0.9 秒后再次暂停。对应时段没有记录到 LynkPlay 发送暂停 HID。
5. 用户隔离测试：关闭 iPhone 蓝牙后不暂停；重新开启后又暂停。关闭时起初无声，但打开车机原生音乐播放一下后，CarPlay 声音和播放均恢复正常。
6. 最新实现分别尝试处理“原生蓝牙干扰”与“车机媒体音源尚未激活”。最新 APK 的这两项尚无实车反馈，不能标为已修复。

以上保存脱敏的结论和必要状态，不提交原始日志、热点密码、手机标识或原始照片。

## 实车验收步骤

请停车测试，不在行驶中操作。

1. 安装同一签名的 APK 可覆盖升级；更换签名须先卸载旧包，设置/配对记录可能丢失。保留原应用 ID，不与 DiPlay 共存。
2. 在 LynkPlay 设置中开启“音频焦点”，重新连接。显式保存的关闭偏好不会被强制覆盖。
3. 保持手机蓝牙开启，不预先打开车机原生音乐；直接播放至少一分钟，检查是否自行出声、是否暂停。
4. 导出新日志，核对 `nativeBluetoothIsolation`、`rendererFocus`、`Bluetooth isolation` 与播放状态。
5. 测试返回桌面、方控播放/暂停、上下曲；另行验证 Siri、电话及结束 CarPlay 后的原生蓝牙连接。

蓝牙隔离会尝试断开原生电话通道；CarPlay 通话不能在未测试前视为可靠替代。若原生蓝牙服务不断重连，达到三次上限后停止反复断开，需要进一步厂商音源/蓝牙策略适配。

## 构建与发布边界

2026-10-07 本地验证：release 构建完成，APK v2 签名验证通过；51 项定向自动化测试通过，覆盖 820A 识别、焦点偏好与界面、媒体回调、焦点转发、蓝牙隔离生命周期/设备范围/重试上限、无线交接及播放器焦点行为。这些是软件验证，不替代实车验收。

源码与自动化测试可公开。APK 采用 release 构建，不代表经过 Apple MFi 认证，也不代表全部功能已实车验收。

APK 包含上游实验性运行身份，接收者可以提取；其未来 iOS 接受性与分发适用性未解决。运行身份与 Android 签名密钥不提交到 Git。请保留上游 GPL/AGPL 来源与许可。领克商标仅用于车型适配标识，不表示官方授权或认可。

发布应同时包含版本说明、APK 校验值与对应源码；正式签名私钥须离线备份，不上传 GitHub。
