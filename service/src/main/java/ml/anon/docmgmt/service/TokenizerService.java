package ml.anon.docmgmt.service;

import com.google.common.collect.Lists;
import de.tudarmstadt.lt.seg.Segment;
import de.tudarmstadt.lt.seg.sentence.ISentenceSplitter;
import de.tudarmstadt.lt.seg.sentence.LineSplitter;
import de.tudarmstadt.lt.seg.sentence.RuleSplitter;
import de.tudarmstadt.lt.seg.token.DiffTokenizer;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by mirco on 15.06.17.
 */
@Service
@Log
public class TokenizerService {


    public static final String SENTENCE_BOUNDARY = UUID.randomUUID().toString();
    private DiffTokenizer tokenizer = new DiffTokenizer();
    private ISentenceSplitter splitter = new RuleSplitter();



    public List<String> tokenize(String string) {
        List<String> tokens = new ArrayList<>();
        List<String> segments = Lists.newArrayList(splitter.init(string).sentences());
        segments.removeIf(s -> StringUtils.isBlank(s));
        for (String sentence : segments) {
            Iterable<String> sentenceTokens = tokenizer.init(sentence).filteredAndNormalizedTokens(3, 0, false, false);
            tokens.addAll(Lists.newArrayList(sentenceTokens).stream().map(s -> s.trim()).collect(Collectors.toList()));
            tokens.add(SENTENCE_BOUNDARY);
        }
        tokens.removeIf(s -> StringUtils.isBlank(s));
        tokens.replaceAll(s -> {
            if (s.equals(SENTENCE_BOUNDARY)) {
                return "";
            }
            return s;
        });
        return tokens;
    }
}
