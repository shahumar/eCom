package lab.org.api.composite.category;

public class CategoryAggregate {

    private final int categoryId;
    private final String name;
    private final String parentId;
    private final String slug;

    public CategoryAggregate() {
        categoryId = 0;
        name = null;
        parentId = null;
        slug = null;
    }

    public CategoryAggregate(int categoryId, String name, String parentId, String slug) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getParentId() {
        return parentId;
    }
}
