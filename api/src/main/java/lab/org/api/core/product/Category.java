package lab.org.api.core.product;


public class Category {

    private int categoryId;
    private String name;
    private String slug;
    private String parentId;

    public Category() {
        categoryId = 0;
        name = null;
        slug = null;
        parentId = null;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Category(int categoryId, String name, String slug, String parentId) {
        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
