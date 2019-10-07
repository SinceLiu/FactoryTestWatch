package com.dinghmcn.android.wificonnectclient.model;

/**
 * 存储获取的状态信息
 */
public class DataModel {

    /**
     * ip : 10.30.39.54:34368
     * socket : null
     * sn : get
     * flag : null
     * light : null
     * accelerometer : null
     * proximity : null
     * magnetometer : null
     * gyroscope : null
     * camera : null
     * wifi : null
     * gps : null
     * bluetooth : null
     * sd : null
     * otg : null
     * sim : null
     * record : null
     * vibrator : null
     * dial : null
     * version : null
     * battery : null
     * disk : null
     * key : null
     * touch : null
     * tip : null
     * screen : null
     * param : null
     * timeout : 0
     * file : null
     */

    /**
     * IP地址
     */
    private String ip;
    /**
     * TCP通讯
     */
    private String socket;
    /**
     * 消息
     */
    private String showMessage;
    /**
     * 串号
     */
    private String sn;
    /**
     * IMEI
     */
    private String imei;
    /**
     * CPU
     */
    private String cpu;
    /**
     * 标志位
     */
    private String flag;
    /**
     * 光线传感器
     */
    private String light;
    /**
     * 加速度传感器
     */
    private String accelerometer;
    /**
     * 距离传感器
     */
    private String proximity;
    /**
     * 磁感应器
     */
    private String magnetometer;
    /**
     * 陀螺仪
     */
    private String gyroscope;
    /**
     * 相机
     */
    private String camera;
    /**
     * WiFi
     */
    private String wifi;
    /**
     * GPS
     */
    private String gps;
    /**
     * 蓝牙
     */
    private String bluetooth;
    /**
     * SD卡
     */
    private String sd;
    /**
     * OTG
     */
    private String otg;
    /**
     * SIM卡
     */
    private String sim;
    /**
     * 录音
     */
    private String record;
    /**
     * 振动
     */
    private String vibrator;
    /**
     * 拨号
     */
    private String dial;
    /**
     * 版本号
     */
    private String version;
    /**
     * 电池
     */
    private String battery;
    /**
     * 存储
     */
    private String disk;
    /**
     * 按键
     */
    private String key;
    /**
     * 触摸
     */
    private String touch;
    /**
     *
     */
    private String tip;
    /**
     * 屏幕
     */
    private String screen;
    /**
     * 关机
     */
    private String shutdown;
    /**
     * 振动时间
     */
    private int timeout;
    /**
     * 相机拍摄的图片
     */
    private byte[] file;

    /**
     * 切屏开关
     */
    private int ScreenOperation;

    /**
     * Gets screenoperation.
     *
     * @return the screenoperation
     */
    public int getScreenoperation() {
        return ScreenOperation;
    }

    /**
     * Sets screenoperation.
     *
     * @param ScreenOperation the screenoperation
     */
    public void setScreenoperation(int ScreenOperation) {
        this.ScreenOperation = ScreenOperation;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets show message.
     *
     * @return the show message
     */
    public String getShowMessage() {
        return showMessage;
    }

    /**
     * Sets show message.
     *
     * @param showMessage the show message
     */
    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }

    /**
     * Gets socket.
     *
     * @return the socket
     */
    public String getSocket() {
        return socket;
    }

    /**
     * Sets socket.
     *
     * @param socket the socket
     */
    public void setSocket(String socket) {
        this.socket = socket;
    }

    /**
     * Gets sn.
     *
     * @return the sn
     */
    public String getSn() {
        return sn;
    }

    /**
     * Sets sn.
     *
     * @param sn the sn
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

    /**
     * @return
     */
    public String getImei() {
        return imei;
    }

    /**
     * @param imei
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * @return
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * @param cpu
     */
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    /**
     * Gets flag.
     *
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Sets flag.
     *
     * @param flag the flag
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * Gets light.
     *
     * @return the light
     */
    public String getLight() {
        return light;
    }

    /**
     * Sets light.
     *
     * @param light the light
     */
    public void setLight(String light) {
        this.light = light;
    }

    /**
     * Gets accelerometer.
     *
     * @return the accelerometer
     */
    public String getAccelerometer() {
        return accelerometer;
    }

    /**
     * Sets accelerometer.
     *
     * @param accelerometer the accelerometer
     */
    public void setAccelerometer(String accelerometer) {
        this.accelerometer = accelerometer;
    }

    /**
     * Gets proximity.
     *
     * @return the proximity
     */
    public String getProximity() {
        return proximity;
    }

    /**
     * Sets proximity.
     *
     * @param proximity the proximity
     */
    public void setProximity(String proximity) {
        this.proximity = proximity;
    }

    /**
     * Gets magnetometer.
     *
     * @return the magnetometer
     */
    public String getMagnetometer() {
        return magnetometer;
    }

    /**
     * Sets magnetometer.
     *
     * @param magnetometer the magnetometer
     */
    public void setMagnetometer(String magnetometer) {
        this.magnetometer = magnetometer;
    }

    /**
     * Gets gyroscope.
     *
     * @return the gyroscope
     */
    public String getGyroscope() {
        return gyroscope;
    }

    /**
     * Sets gyroscope.
     *
     * @param gyroscope the gyroscope
     */
    public void setGyroscope(String gyroscope) {
        this.gyroscope = gyroscope;
    }

    /**
     * Gets camera.
     *
     * @return the camera
     */
    public String getCamera() {
        return camera;
    }

    /**
     * Sets camera.
     *
     * @param camera the camera
     */
    public void setCamera(String camera) {
        this.camera = camera;
    }

    /**
     * Gets wifi.
     *
     * @return the wifi
     */
    public String getWifi() {
        return wifi;
    }

    /**
     * Sets wifi.
     *
     * @param wifi the wifi
     */
    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    /**
     * Gets gps.
     *
     * @return the gps
     */
    public String getGps() {
        return gps;
    }

    /**
     * Sets gps.
     *
     * @param gps the gps
     */
    public void setGps(String gps) {
        this.gps = gps;
    }

    /**
     * Gets bluetooth.
     *
     * @return the bluetooth
     */
    public String getBluetooth() {
        return bluetooth;
    }

    /**
     * Sets bluetooth.
     *
     * @param bluetooth the bluetooth
     */
    public void setBluetooth(String bluetooth) {
        this.bluetooth = bluetooth;
    }

    /**
     * Gets sd.
     *
     * @return the sd
     */
    public String getSd() {
        return sd;
    }

    /**
     * Sets sd.
     *
     * @param sd the sd
     */
    public void setSd(String sd) {
        this.sd = sd;
    }

    /**
     * Gets otg.
     *
     * @return the otg
     */
    public String getOtg() {
        return otg;
    }

    /**
     * Sets otg.
     *
     * @param otg the otg
     */
    public void setOtg(String otg) {
        this.otg = otg;
    }

    /**
     * Gets sim.
     *
     * @return the sim
     */
    public String getSim() {
        return sim;
    }

    /**
     * Sets sim.
     *
     * @param sim the sim
     */
    public void setSim(String sim) {
        this.sim = sim;
    }

    /**
     * Gets record.
     *
     * @return the record
     */
    public String getRecord() {
        return record;
    }

    /**
     * Sets record.
     *
     * @param record the record
     */
    public void setRecord(String record) {
        this.record = record;
    }

    /**
     * Gets vibrator.
     *
     * @return the vibrator
     */
    public String getVibrator() {
        return vibrator;
    }

    /**
     * Sets vibrator.
     *
     * @param vibrator the vibrator
     */
    public void setVibrator(String vibrator) {
        this.vibrator = vibrator;
    }

    /**
     * Gets dial.
     *
     * @return the dial
     */
    public String getDial() {
        return dial;
    }

    /**
     * Sets dial.
     *
     * @param dial the dial
     */
    public void setDial(String dial) {
        this.dial = dial;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets battery.
     *
     * @return the battery
     */
    public String getBattery() {
        return battery;
    }

    /**
     * Sets battery.
     *
     * @param battery the battery
     */
    public void setBattery(String battery) {
        this.battery = battery;
    }

    /**
     * Gets disk.
     *
     * @return the disk
     */
    public String getDisk() {
        return disk;
    }

    /**
     * Sets disk.
     *
     * @param disk the disk
     */
    public void setDisk(String disk) {
        this.disk = disk;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets touch.
     *
     * @return the touch
     */
    public String getTouch() {
        return touch;
    }

    /**
     * Sets touch.
     *
     * @param touch the touch
     */
    public void setTouch(String touch) {
        this.touch = touch;
    }

    /**
     * Gets tip.
     *
     * @return the tip
     */
    public String getTip() {
        return tip;
    }

    /**
     * Sets tip.
     *
     * @param tip the tip
     */
    public void setTip(String tip) {
        this.tip = tip;
    }

    /**
     * Gets screen.
     *
     * @return the screen
     */
    public String getScreen() {
        return screen;
    }

    /**
     * Sets screen.
     *
     * @param screen the screen
     */
    public void setScreen(String screen) {
        this.screen = screen;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public int getTimeout() {
        return timeout > 0 ? timeout : 1;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Get file byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * Sets file.
     *
     * @param file the file
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * Gets shutdown.
     *
     * @return the shutdown
     */
    public String getShutdown() {
        return shutdown;
    }

    /**
     * Sets shutdown.
     *
     * @param shutdown the shutdown
     */
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }
}
