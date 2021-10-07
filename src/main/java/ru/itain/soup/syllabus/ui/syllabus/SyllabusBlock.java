package ru.itain.soup.syllabus.ui.syllabus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusBlock {
    String head;
    List<SyllabusArticle> articles;
    SyllabusRow total;
}
