package nctu.cs.cgv.itour.object;

import java.util.LinkedHashMap;

import static nctu.cs.cgv.itour.MyApplication.CATEGORY_NOT_FOUND;

public class SpotCategory {
    public LinkedHashMap<String, String> spotCategory;
    public SpotCategory() {
        spotCategory = new LinkedHashMap<>();
        spotCategory.put("三協成博物館", "歷史");
        spotCategory.put("小白宮", "歷史");
        spotCategory.put("之間茶食器", "美食");
        spotCategory.put("文化阿給", "美食");
        spotCategory.put("木下靜涯紀念公園", "歷史");
        spotCategory.put("可口魚丸", "美食");
        spotCategory.put("白樓故事牆", "歷史");
        spotCategory.put("合益魚酥", "美食");
        spotCategory.put("多田榮吉故居", "歷史");
        spotCategory.put("百葉溫州大餛飩", "美食");
        spotCategory.put("老牌阿給", "美食");
        spotCategory.put("姑娘樓", "歷史");
        spotCategory.put("牧師樓", "歷史");
        spotCategory.put("金色水岸", "休閒");
        spotCategory.put("金色水岸自行車道", "休閒");
        spotCategory.put("長堤咖啡餐館", "美食");
        spotCategory.put("阿婆鐵蛋", "美食");
        spotCategory.put("阿媽的酸梅湯", "美食");
        spotCategory.put("信不信由你搜奇博物館", "休閒");
        spotCategory.put("紅毛城", "歷史");
        spotCategory.put("紅樓中餐廳", "美食");
        spotCategory.put("英國領事館", "歷史");
        spotCategory.put("英專夜市", "美食");
        spotCategory.put("重建街戀愛巷", "休閒");
        spotCategory.put("桂橡花園", "美食");
        spotCategory.put("海風餐廳", "美食");
        spotCategory.put("海關碼頭", "交通");
        spotCategory.put("真理大學", "歷史");
        spotCategory.put("真理大學禮拜堂", "宗教");
        spotCategory.put("馬偕上岸處", "歷史");
        spotCategory.put("馬偕故居(馬偕紀念館)", "歷史");
        spotCategory.put("馬偕租屋處", "歷史");
        spotCategory.put("馬偕銅像", "歷史");
        spotCategory.put("得忌利士洋行", "歷史");
        spotCategory.put("教士會館", "宗教");
        spotCategory.put("淡水文化園區-殼牌倉庫", "歷史");
        spotCategory.put("淡水老街", "休閒");
        spotCategory.put("淡水捷運站", "交通");
        spotCategory.put("淡水渡船頭", "交通");
        spotCategory.put("淡水榕樹道", "休閒");
        spotCategory.put("淡水禮拜堂", "宗教");
        spotCategory.put("清水巖", "宗教");
        spotCategory.put("理學堂大書院", "歷史");
        spotCategory.put("許義魚酥", "美食");
        spotCategory.put("源味本舖現烤蛋糕", "美食");
        spotCategory.put("滬尾偕醫館", "歷史");
        spotCategory.put("漁業生活文化影像館", "休閒");
        spotCategory.put("蒸汽火車BK20", "歷史");
        spotCategory.put("福佑宮", "宗教");
        spotCategory.put("鄞山寺", "宗教");
        spotCategory.put("龍山寺", "宗教");
        spotCategory.put("藝術工坊", "休閒");
        spotCategory.put("藝術穿堂", "休閒");
    }
    public String getCategory(String location) {
        if (spotCategory.containsKey(location)) return spotCategory.get(location);
        else return CATEGORY_NOT_FOUND;
    }
}
