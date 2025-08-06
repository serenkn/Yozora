package com.example.yozora.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 投稿フォーム
@Data
public class PostCreateForm {

    private Integer id; // 投稿ID

    private Integer userId;// ユーザID

    @NotBlank(message = "タイトルを入力してください")
    private String title;// 投稿タイトル

    @NotBlank(message = "内容を入力してください")
    private String text;// 投稿詳細

    @NotBlank(message = "住所が取得できませんでした")
    private String address;// 住所

    @NotNull(message = "位置情報が取得できませんでした")
    private Double latitude;// 緯度

    @NotNull(message = "位置情報が取得できませんでした")
    private Double longitude;// 経度

    private List<MultipartFile> imageFiles; // 投稿画像ファイルのリスト

    private List<String> imageUrls;// 投稿画像表示用

    private String pageType;

}
