物件標記APP
=============================

<p float="left">
  <img src="/2.jpg" width="250" />
  <img src="/5.jpg" width="250" /> 
  <img src="/3.jpg" width="250" />
  <img src="/1.jpg" width="250" />
  <img src="/4.jpg" width="250" />
</p>

簡介
------------

- 使用手機相機拍攝物件
- 軟體能使用內建物件框選模型偵測出照片中的物件
- 軟體將根據每個已辨識的物件分別裁切成單一特寫照片
- 每個物件可供使用者自行輸入1~3個標籤
- 完成後可上傳至雲端資料庫儲存

使用套件
------------

- [ML Kit](https://developers.google.com/ml-kit)
- [Firebase Database](https://firebase.google.com/docs/database)

資料庫架構
------------

- 圖片已JPEG格式儲存於firebase storeage，檔名格式為“JPEG_日期_時間”,如“JPEG_20201009_141200”
- 使用者標記資料已JSON格式儲存於firebase realtime database，格式如下

欄位解釋:
```
    "標記物件隨機生成ID(英數混合)" : {
      "bottom" : "物件框底部y軸數值(int)",
      "label1" : "標籤1（string)",
      "label2" : "標籤2（string)",
      "label3" : "標籤3（string)",
      "left" : "物件框左邊x軸數值(int)",
      "right" : "物件框右邊x軸數值(int)",
      "top" : "物件框頂部y軸數值(int)",
      "uid" : "該物件對應照片檔名（string)"
    }
```

儲存範例:

```
    "-MlJ27Vsisfl9O9TcNfY" : {
      "bottom" : "2990",
      "label1" : "鍵盤",
      "label2" : "",
      "label3" : "",
      "left" : "213",
      "right" : "1159",
      "top" : "2040",
      "uid" : "0"
    }
```
展示影片
------------
[Youtube連結](https://youtu.be/3dlyRlCIImk)

預計增加功能
------------
 - [ ] 螢幕手動框選（含縮放）
 - [ ] 標記格式遵循標準格式（coco格式）
 - [ ] 同步帳號接續標記功能（同步照片及套用歷史標記）
 - [ ] 可手動增加標記項目數量
 - [ ] beta版上架play商店
