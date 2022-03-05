package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.common.repository.FindAllByDepartmentRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;

import java.util.List;

@Transactional
public interface ArticleRepository extends CrudRepository<Article, Long>, JpaSpecificationExecutor<Article>, FindAllByDepartmentRepository<Article> {

    @Query("Select a from Article a where lower(a.name) like %:name%")
    List<Article> findArticlesByNameLike(String name);

    @Override
    List<Article> findAll();

}
