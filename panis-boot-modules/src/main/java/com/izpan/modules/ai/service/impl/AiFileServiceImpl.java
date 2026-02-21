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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.holder.GlobalUserHolder;
import com.izpan.modules.ai.domain.dto.file.AiFileUploadDTO;
import com.izpan.modules.ai.domain.entity.AiFileUpload;
import com.izpan.modules.ai.repository.mapper.AiFileUploadMapper;
import com.izpan.modules.ai.service.IAiFileService;
import com.izpan.starter.oss.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AI文件 Service 服务接口实现层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.service.impl.AiFileServiceImpl
 * @CreateTime 2024-12-20
 */
@Slf4j
@Service
public class AiFileServiceImpl extends ServiceImpl<AiFileUploadMapper, AiFileUpload> implements IAiFileService {

    @Autowired
    private OssService ossService;

    @Override
    public List<String> uploadFiles(AiFileUploadDTO uploadDTO) {
        List<String> fileUrls = new ArrayList<>();

        if (uploadDTO.getFileNames() != null) {
            for (String fileName : uploadDTO.getFileNames()) {
                try {
                    String fileUrl = ossService.putFile("ai-files", fileName, new ByteArrayInputStream(new byte[0])).getPath();
                    fileUrls.add(fileUrl);

                    AiFileUpload fileUpload = AiFileUpload.builder()
                            .fileName(fileName)
                            .fileUrl(fileUrl)
                            .fileSize(0L)
                            .fileType(getFileType(fileName))
                            .sessionId(uploadDTO.getSessionId())
                            .build();

                    baseMapper.insert(fileUpload);
                } catch (Exception e) {
                    log.error("文件上传失败: {}", fileName, e);
                }
            }
        }

        return fileUrls;
    }

    @Override
    public void deleteFiles(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                try {
                    AiFileUpload fileUpload = baseMapper.selectById(id);
                    if (fileUpload != null) {
                        ossService.removeFile(fileUpload.getFileUrl());
                    }
                    baseMapper.deleteById(id);
                } catch (Exception e) {
                    log.error("删除文件失败: {}", id, e);
                }
            }
        }
    }

    private String getFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif" -> "image";
            case "pdf" -> "pdf";
            case "doc", "docx" -> "word";
            case "txt" -> "text";
            default -> "unknown";
        };
    }
}
