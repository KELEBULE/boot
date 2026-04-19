/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.izpan.modules.ai.service.impl;

import com.izpan.modules.ai.service.IAiFileParseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * AI文件解析 Service 服务接口实现层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.service.impl.AiFileParseServiceImpl
 * @CreateTime 2024-12-20
 */
@Slf4j
@Service
public class AiFileParseServiceImpl implements IAiFileParseService {

    private final Tika tika = new Tika();

    @Override
    public String parseFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(fileName);

        log.info("开始解析文件: {}, 类型: {}", fileName, fileType);

        try (InputStream inputStream = file.getInputStream()) {
            if ("image".equals(fileType)) {
                return parseImageFile(fileName);
            } else {
                return parseDocumentFile(inputStream, fileName);
            }
        } catch (Exception e) {
            log.error("文件解析失败: {}", fileName, e);
            throw new Exception("文件解析失败: " + e.getMessage());
        }
    }

    private String getFileType(String fileName) {
        if (fileName == null) {
            return "unknown";
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp" -> "image";
            case "pdf" -> "pdf";
            case "doc", "docx" -> "word";
            case "xls", "xlsx" -> "excel";
            case "ppt", "pptx" -> "powerpoint";
            case "txt" -> "text";
            default -> "document";
        };
    }

    private String parseImageFile(String fileName) {
        StringBuilder content = new StringBuilder();
        content.append("【图片文件】\n");
        content.append("文件名: ").append(fileName).append("\n");
        content.append("说明: 这是一个图片文件，无法提取文本内容。\n");
        content.append("建议: 如果需要分析图片内容，请使用支持图片识别的AI模型。");
        return content.toString();
    }

    private String parseDocumentFile(InputStream inputStream, String fileName) throws Exception {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        context.set(Parser.class, parser);

        parser.parse(inputStream, handler, metadata, context);

        String content = handler.toString();
        if (content == null || content.trim().isEmpty()) {
            return "文件内容为空或无法提取文本内容。";
        }

        StringBuilder result = new StringBuilder();
        result.append("【文件信息】\n");
        result.append("文件名: ").append(fileName).append("\n");

        String contentType = metadata.get("Content-Type");
        if (contentType != null) {
            result.append("内容类型: ").append(contentType).append("\n");
        }

        result.append("\n【文件内容】\n");
        result.append(content.trim());

        return result.toString();
    }
}
