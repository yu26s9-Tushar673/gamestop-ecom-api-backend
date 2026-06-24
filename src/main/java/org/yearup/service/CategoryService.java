package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.exception.ResourceNotFoundException;
import org.yearup.models.Category;
import org.yearup.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService
{
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository)
    {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories()
    {
        return categoryRepository.findAll();
    }

    public Category getById(int categoryId)
    {
        // get category by id
        return categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category Not Found: " + categoryId));
    }

    public Category create(Category category)
    {
       return categoryRepository.save(category);
    }

    public Category update(int categoryId, Category updatedCategory)
    {
        return categoryRepository.findById(categoryId).map(existing -> {
            existing.setCategoryId(updatedCategory.getCategoryId());
            existing.setName(updatedCategory.getName());
            existing.setDescription(updatedCategory.getDescription());
            return categoryRepository.save(existing);
        }).orElseThrow(() -> new ResourceNotFoundException("Category Not Found: " + categoryId));
    }

    public void delete(int categoryId)
    {
        categoryRepository.deleteById(categoryId);
    }
}