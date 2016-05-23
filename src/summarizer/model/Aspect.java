package summarizer.model;

/**
 * Created by atone on 15/6/12.
 * This is the class that respect to an aspect
 *
 */
public class Aspect implements Comparable<Aspect> {
    public int id;
    public String name;
    public int position; // the position of the aspect in original review

    public Aspect(int id) {
        assert id >= 1 && id <= 17;
        this.id = id;
        this.name = nameList[id - 1];
    }

    public static String getAspect(String content, int aspectID) {
        assert aspectID >= 1 && aspectID <= 17;
        String[] aspects = descriptionList[aspectID - 1].split(" ");
        for (String aspect : aspects) {
            if (content.contains(aspect)) {
                return aspect;
            }
        }
        return "";
    }

    private static String[] nameList = {"外观", "质量", "屏幕", "性价比", "系统", "软件", "操控", "电池",
            "键盘", "信号", "短信", "界面", "输入法", "款式", "照相", "音质", "存储"};

    private static String[] descriptionList = {
            "外观 外形 设计 外型 外壳 外表",
            "质量 材质 手感 质感 作工 做工",
            "屏幕 触摸屏 显示屏 分辨率 led 触摸板 液晶屏 电阻屏 显示 触屏",
            "性价比 价位 价钱 价格 售价",
            "系统 稳定性 性能 速度 操作系统 兼容性",
            "软件 导航 wifi",
            "操控 操控性 操作性 操作 触控",
            "电池 待机 电量 续航 耗电",
            "键盘 按键 功能键 按钮",
            "信号 网络 蓝牙 通话 天线 通信 通讯",
            "短信 彩信",
            "界面 画面 画质 ui",
            "输入法 手写 输入",
            "机型 机身 款式 样式",
            "照相 摄像 照像 相机 拍照 镜头 像素 闪光灯 摄像头 照相机 录音",
            "音效 音色 音质 话筒 听筒 扬声器 喇叭 话音 音响 语音 立体声",
            "存储 内存 内存卡 存储卡 储存卡 扩展卡"
    };

    @Override
    public String toString() {
        return String.format("(%s, %d)", name, id);
    }

    @Override
    public int compareTo(Aspect o) {
        return this.position - o.position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aspect aspect = (Aspect) o;

        return id == aspect.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
