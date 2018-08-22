package com.example.zz.zhihu;

import java.util.List;

/**
 * Created by zz on 2017/12/22.
 */

public class GetLastestNews {
    /**
     * date : 20171222
     * stories : [{"images":["https://pic3.zhimg.com/v2-dc20672b4f2a36b38c4df401de916fda.jpg"],"type":0,"id":9662049,"ga_prefix":"122222","title":"小事 · 一个少女的史诗"},{"images":["https://pic2.zhimg.com/v2-81d34556c562617711068320c10e0831.jpg"],"type":0,"id":9662343,"ga_prefix":"122221","title":"后悔当时没贡献一张电影票"},{"images":["https://pic2.zhimg.com/v2-911fa58d12a3de679ffb8298d96bf9dd.jpg"],"type":0,"id":9661687,"ga_prefix":"122218","title":"业主维权界的网红案例，官司赢了，物业跑了......"},{"images":["https://pic2.zhimg.com/v2-877baca7bc73894a8ef109930a9e82a1.jpg"],"type":0,"id":9662324,"ga_prefix":"122218","title":"被怀疑是骗子公司的 Magic Leap，终于发布了初代产品"},{"title":"圣诞来了 · 不玩起来可怎么行","ga_prefix":"122216","images":["https://pic1.zhimg.com/v2-26ffe84845cf87311ac49444c1161f4c.jpg"],"multipic":true,"type":0,"id":9662155},{"images":["https://pic2.zhimg.com/v2-a5511257429388f7f85a674d0dac2e15.jpg"],"type":0,"id":9662254,"ga_prefix":"122215","title":"不打疫苗可以，但拿「基因检测就能取代」来反对就有点过了"},{"images":["https://pic4.zhimg.com/v2-c6d1cb436f35288373790286c822a7d7.jpg"],"type":0,"id":9661673,"ga_prefix":"122214","title":"你上次吃的芥末是黄色的还是绿色的？"},{"images":["https://pic2.zhimg.com/v2-06054480881a46f1300a60bb9b030fe5.jpg"],"type":0,"id":9662263,"ga_prefix":"122213","title":"带着头套的「王尼玛」，可以是一个人，也可以是所有人"},{"images":["https://pic1.zhimg.com/v2-90b55a141760709e6fc3ed83747ae1f8.jpg"],"type":0,"id":9662066,"ga_prefix":"122212","title":"大误 · 把皮卡丘的耳朵塞到插座里它会电死吗？"},{"images":["https://pic3.zhimg.com/v2-ff8d6bd24d788d5787f201c4b67db48a.jpg"],"type":0,"id":9662229,"ga_prefix":"122210","title":"杭州保姆纵火案：嫌疑人律师开庭前就已准备好中途退庭了"},{"images":["https://pic1.zhimg.com/v2-21723afeb45ab442bd74bc18caa2afb8.jpg"],"type":0,"id":9661537,"ga_prefix":"122209","title":"「除非找到那个对的人，不然我是不会恋爱的」"},{"images":["https://pic4.zhimg.com/v2-294ed10e006699db1d8bfa5108085b77.jpg"],"type":0,"id":9662209,"ga_prefix":"122208","title":"江歌案一审，陈世峰被判有期徒刑 20 年，是不是判轻了？"},{"images":["https://pic2.zhimg.com/v2-be9075595f7ee1faee8e6de5b02b7d11.jpg"],"type":0,"id":9662219,"ga_prefix":"122207","title":"我从没有对张惠妹的这张专辑这么失望过"},{"images":["https://pic3.zhimg.com/v2-f40f8f87aee7996db8ee8c56f43d3602.jpg"],"type":0,"id":9662214,"ga_prefix":"122207","title":"2017 年，谁是中国最具商业价值的明星？"},{"images":["https://pic3.zhimg.com/v2-d3515c8ea366d9242995a9f58df30422.jpg"],"type":0,"id":9662077,"ga_prefix":"122207","title":"水滴直播争议结束，但网络隐私的焦虑为何还没有结束？"},{"images":["https://pic1.zhimg.com/v2-214e81bd5083bb0b96dd6212f1b590d0.jpg"],"type":0,"id":9662173,"ga_prefix":"122206","title":"瞎扯 · 如何正确地吐槽"}]
     * top_stories : [{"image":"https://pic3.zhimg.com/v2-f01ac41298a571f6ba83252e3e1c0efa.jpg","type":0,"id":9662324,"ga_prefix":"122218","title":"被怀疑是骗子公司的 Magic Leap，终于发布了初代产品"},{"image":"https://pic3.zhimg.com/v2-ddfabf97326632c3d4a6998357001b12.jpg","type":0,"id":9662155,"ga_prefix":"122216","title":"圣诞来了 · 不玩起来可怎么行"},{"image":"https://pic3.zhimg.com/v2-67b76052e1f5bcfc27f0027ca3f806e2.jpg","type":0,"id":9662254,"ga_prefix":"122215","title":"不打疫苗可以，但拿「基因检测就能取代」来反对就有点过了"},{"image":"https://pic3.zhimg.com/v2-ea60ca15121633b4f9d5c0206ae08f26.jpg","type":0,"id":9662263,"ga_prefix":"122213","title":"带着头套的「王尼玛」，可以是一个人，也可以是所有人"},{"image":"https://pic1.zhimg.com/v2-226c86cf655a0b6d95af17e13d9f22b8.jpg","type":0,"id":9662229,"ga_prefix":"122210","title":"杭州保姆纵火案：嫌疑人律师开庭前就已准备好中途退庭了"}]
     */

    private String date;
    private List<StoriesBean> stories;
    private List<TopStoriesBean> top_stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<TopStoriesBean> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<TopStoriesBean> top_stories) {
        this.top_stories = top_stories;
    }

    public static class StoriesBean {
        /**
         * images : ["https://pic3.zhimg.com/v2-dc20672b4f2a36b38c4df401de916fda.jpg"]
         * type : 0
         * id : 9662049
         * ga_prefix : 122222
         * title : 小事 · 一个少女的史诗
         * multipic : true
         */

        private int type;
        private int id;
        private String ga_prefix;
        private String title;
        private boolean multipic;
        private List<String> images;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isMultipic() {
            return multipic;
        }

        public void setMultipic(boolean multipic) {
            this.multipic = multipic;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class TopStoriesBean {
        /**
         * image : https://pic3.zhimg.com/v2-f01ac41298a571f6ba83252e3e1c0efa.jpg
         * type : 0
         * id : 9662324
         * ga_prefix : 122218
         * title : 被怀疑是骗子公司的 Magic Leap，终于发布了初代产品
         */

        private String image;
        private int type;
        private int id;
        private String ga_prefix;
        private String title;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
