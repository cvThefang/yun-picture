package com.thefang.yunpicturebackend.utils;

/**
 * @Description 颜色相似度计算工具类
 * 使用 欧几里得距离 算法计算颜色相似度 distance = sqrt( (r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2 )
 * @Author Thefang
 * @Create 2025/1/12
 */
public class ColorTransformUtils {


    private ColorTransformUtils() {
        // 工具类不需要实例化
    }

    /**
     * 获取标准颜色（将数据万象的颜色转换为标准颜色）
     * 如果总长度小于7，则在最后补0补齐到长度为7。
     *
     * @param color 颜色字符串
     * @return 标准颜色
     */
    public static String getStandardColor(String color) {
        // 检查是否已经有7或8个字符，如果有则直接返回
        if (color.length() == 7) { // 包括前缀
            return color;
        }

        // 确保前缀是有效的 '0x' 或 '#'
        String prefix = "";
        if (color.startsWith("0x") || color.startsWith("#")) {
            prefix = color.substring(0, 2);
            color = color.substring(2);
        } else {
            // 如果没有有效的前缀，默认添加 '#' 前缀
            prefix = "#";
        }

        // 初始化 RGB 通道
        StringBuilder r = new StringBuilder();
        StringBuilder g = new StringBuilder();
        StringBuilder b = new StringBuilder();

        int length = color.length();

        // 分配字符到 RGB 通道，并扩展到两位
        switch (length) {
            case 6:
                r.append(color, 0, 2);
                g.append(color, 2, 4);
                b.append(color, 4, 6);
                break;
            case 5:
                // 红色通道固定两位
                r.append(color, 0, 2);
                // 假设绿色通道丢失了一位
                g.append(color.charAt(2)).append(color.charAt(2));
                // 蓝色通道剩余部分
                b.append(color.substring(3));
                break;
            case 4:
                // 扩展红色通道
                r.append(color.charAt(0)).append(color.charAt(0));
                // 扩展绿色通道
                g.append(color.charAt(1)).append(color.charAt(1));
                // 扩展蓝色通道
                b.append(color.charAt(2)).append(color.charAt(2));
                break;
            case 3:
                // 扩展红色通道
                r.append(color.charAt(0)).append(color.charAt(0));
                // 扩展绿色通道
                g.append(color.charAt(1)).append(color.charAt(1));
                // 扩展蓝色通道
                b.append(color.charAt(2)).append(color.charAt(2));
                break;
            default:
                // 对于其他情况，假设每个通道只有一个字符并扩展
                for (int i = 0; i < Math.min(length, 3); i++) {
                    char ch = color.charAt(i);
                    if (i == 0) r.append(ch).append(ch);
                    else if (i == 1) g.append(ch).append(ch);
                    else b.append(ch).append(ch);
                }
                // 如果长度小于3，继续扩展剩余的通道
                while (r.length() < 2) r.insert(0, '0');
                while (g.length() < 2) g.insert(0, '0');
                while (b.length() < 2) b.insert(0, '0');
        }
        // 返回带有前缀的完整颜色值
        return prefix + r + g + b;
    }

    // 示例用法
    public static void main(String[] args) {
        // 示例用法
        String[] colors = {"0x123", "#abc", "0x12", "#ab", "0x1", "#a", "0x1234", "0x102030", "0xe0", "#ff", "0xabc", "#12345", "0xffffff", "0xe0e0e0", "0xe000"};
        for (String color : colors) {
            System.out.println("原始颜色值: " + color + ", 标准化后的颜色值: " + getStandardColor(color));
        }
    }

}
