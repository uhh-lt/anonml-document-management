package ml.anon.docmgmt.service;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import ml.anon.docmgmt.controller.DocumentRepository;
import ml.anon.docmgmt.extraction.ExtractionResult;
import ml.anon.docmgmt.extraction.PDFExtractor;
import ml.anon.documentmanagement.model.Document;
import ml.anon.documentmanagement.model.DocumentState;
import ml.anon.documentmanagement.model.FileType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Service
public class DocumentImportService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DocumentImportService.class);

    @Autowired
    private TokenizerService tokenizerService;
    @Autowired
    private DocumentRepository repo;

    @SneakyThrows
    public Document doImport(byte[] file, String fileName) {
        PDFExtractor extractor = new PDFExtractor();

        File tempFile = File.createTempFile(fileName, null);
        FileUtils.writeByteArrayToFile(tempFile, file);
        FileInputStream inStream = new FileInputStream(tempFile);
        ExtractionResult extract = extractor.extract(inStream);
        if (extract != null) {
            List<String> text = extract.getPaginated();
            List<String> chunked = tokenizerService.tokenize(extract.getFullText());

            return repo.save(Document.builder().file(file).fileName(fileName).text(text).fullText(extract.getFullText()).state(DocumentState.UPLOADED)
                    .displayableText(extract.getFullText().replaceAll("\n", "<br/>"))
                    .chunks(chunked).originalFileType(extract.getType()).build());
        } else {

            FileUtils.copyInputStreamToFile(inStream, tempFile);
            String text = FileUtils.readFileToString(tempFile);
            return repo.save(Document.builder().file(file).fileName(fileName).text(Lists.newArrayList(text)).fullText(text).state(DocumentState.UPLOADED).displayableText(text).chunks(tokenizerService.tokenize(text)).originalFileType(FileType.TXT).build());
        }

    }


}
