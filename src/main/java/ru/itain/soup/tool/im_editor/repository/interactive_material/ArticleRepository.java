package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.common.repository.FindAllByDepartmentRepository;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;

import java.util.List;
import java.util.Optional;

@Transactional
public interface ArticleRepository extends CrudRepository<Article, Long>, JpaSpecificationExecutor<Article>, FindAllByDepartmentRepository<Article> {

    @Query("Select a from Article a where lower(a.name) like %:name%")
    List<Article> findArticlesByNameLike(String name);


}
