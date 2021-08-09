package ru.itain.soup.common.ui.view.tutor.service;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleBlockService {
	public List<ArticleBlock> getBlocks(Article article) {
		String html = article.getContent();
		Document doc = Jsoup.parse(html);
		Elements blockElements = doc.getElementsByAttributeValueStarting("id", "block_");
		return blockElements.stream().flatMap(block -> {
			Attributes attributes = block.attributes();
			List<Attribute> blockNames = attributes.asList().stream().filter(it -> "block_name".equals(it.getKey())).collect(Collectors.toList());
			return blockNames.stream().map(it -> new ArticleBlock(it.getValue(), block.toString()))
					.collect(Collectors.toList())
					.stream();
		})
				.collect(Collectors.toList());

	}

	public static class ArticleBlock {
		private final String name;
		private final String content;

		public ArticleBlock(String name, String content) {
			this.name = name;
			this.content = content;
		}

		public String getName() {
			return name;
		}

		public String getContent() {
			return content;
		}
	}
}
