package com.example.customer_food.Model;

public class CategoriesItem {
    private String categoryName;
    private String categoryId;
    private String categoryImage;


    public CategoriesItem(String categoryName,
                          String categoryId
//            , String categoryImage

    ) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
//        this.categoryImage = categoryImage;

    }

    public String getCategoryName() {
        return categoryName;
    }
    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public void setCategoryId(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

}
