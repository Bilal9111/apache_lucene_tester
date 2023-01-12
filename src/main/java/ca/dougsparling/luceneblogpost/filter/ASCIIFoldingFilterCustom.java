package ca.dougsparling.luceneblogpost.filter;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;

public final class ASCIIFoldingFilterCustom extends TokenFilter {
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncAttr;
    private final boolean preserveOriginal;
    private char[] output;
    private int outputPos;
    private AttributeSource.State state;

    public ASCIIFoldingFilterCustom(TokenStream input) {
        this(input, false);
    }

    public ASCIIFoldingFilterCustom(TokenStream input, boolean preserveOriginal) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
        this.posIncAttr = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
        this.output = new char[512];
        this.preserveOriginal = preserveOriginal;
    }

    public boolean isPreserveOriginal() {
        return this.preserveOriginal;
    }

    public boolean incrementToken() throws IOException {
        if (this.state != null) {
            assert this.preserveOriginal : "state should only be captured if preserveOriginal is true";

            this.restoreState(this.state);
            this.posIncAttr.setPositionIncrement(0);
            this.state = null;
            return true;
        } else if (!this.input.incrementToken()) {
            return false;
        } else {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();

            for(int i = 0; i < length; ++i) {
                char c = buffer[i];
                if (c >= 128) {
                    this.foldToASCII(buffer, length);
                    this.termAtt.copyBuffer(this.output, 0, this.outputPos);
                    break;
                }
            }

            return true;
        }
    }

    public void reset() throws IOException {
        super.reset();
        this.state = null;
    }

    public void foldToASCII(char[] input, int length) {
        if (this.preserveOriginal) {
            this.state = this.captureState();
        }

        int maxSizeNeeded = 4 * length;
        if (this.output.length < maxSizeNeeded) {
            this.output = new char[ArrayUtil.oversize(maxSizeNeeded, 2)];
        }

        this.outputPos = foldToASCII(input, 0, this.output, 0, length);
    }

    public static final int foldToASCII(char[] input, int inputPos, char[] output, int outputPos, int length) {
        int end = inputPos + length;

        for(int pos = inputPos; pos < end; ++pos) {
            char c = input[pos];
            if (c < 128) {
                output[outputPos++] = c;
            } else {
                switch (c) {
                    case '«':
                    case '»':
                    case '“':
                    case '”':
                    case '„':
                    case '″':
                    case '‶':
                    case '❝':
                    case '❞':
                    case '❮':
                    case '❯':
                    case '＂':
                        //output[outputPos++] = '"';
                        break;
                    case '²':
                    case '₂':
                    case '②':
                    case '⓶':
                    case '❷':
                    case '➁':
                    case '➋':
                    case '２':
                        output[outputPos++] = '2';
                        break;
                    case '³':
                    case '₃':
                    case '③':
                    case '⓷':
                    case '❸':
                    case '➂':
                    case '➌':
                    case '３':
                        output[outputPos++] = '3';
                        break;
                    case '¹':
                    case '₁':
                    case '①':
                    case '⓵':
                    case '❶':
                    case '➀':
                    case '➊':
                    case '１':
                        output[outputPos++] = '1';
                        break;
                    case 'À':
                    case 'Á':
                    case 'Â':
                    case 'Ã':
                    case 'Ä':
                    case 'Å':
                    case 'Ā':
                    case 'Ă':
                    case 'Ą':
                    case 'Ə':
                    case 'Ǎ':
                    case 'Ǟ':
                    case 'Ǡ':
                    case 'Ǻ':
                    case 'Ȁ':
                    case 'Ȃ':
                    case 'Ȧ':
                    case 'Ⱥ':
                    case 'ᴀ':
                    case 'Ḁ':
                    case 'Ạ':
                    case 'Ả':
                    case 'Ấ':
                    case 'Ầ':
                    case 'Ẩ':
                    case 'Ẫ':
                    case 'Ậ':
                    case 'Ắ':
                    case 'Ằ':
                    case 'Ẳ':
                    case 'Ẵ':
                    case 'Ặ':
                    case 'Ⓐ':
                    case 'Ａ':
                        output[outputPos++] = 'A';
                        break;
                    case 'Æ':
                    case 'Ǣ':
                    case 'Ǽ':
                    case 'ᴁ':
                        output[outputPos++] = 'A';
                        output[outputPos++] = 'E';
                        break;
                    case 'Ç':
                    case 'Ć':
                    case 'Ĉ':
                    case 'Ċ':
                    case 'Č':
                    case 'Ƈ':
                    case 'Ȼ':
                    case 'ʗ':
                    case 'ᴄ':
                    case 'Ḉ':
                    case 'Ⓒ':
                    case 'Ｃ':
                        output[outputPos++] = 'C';
                        break;
                    case 'È':
                    case 'É':
                    case 'Ê':
                    case 'Ë':
                    case 'Ē':
                    case 'Ĕ':
                    case 'Ė':
                    case 'Ę':
                    case 'Ě':
                    case 'Ǝ':
                    case 'Ɛ':
                    case 'Ȅ':
                    case 'Ȇ':
                    case 'Ȩ':
                    case 'Ɇ':
                    case 'ᴇ':
                    case 'Ḕ':
                    case 'Ḗ':
                    case 'Ḙ':
                    case 'Ḛ':
                    case 'Ḝ':
                    case 'Ẹ':
                    case 'Ẻ':
                    case 'Ẽ':
                    case 'Ế':
                    case 'Ề':
                    case 'Ể':
                    case 'Ễ':
                    case 'Ệ':
                    case 'Ⓔ':
                    case 'ⱻ':
                    case 'Ｅ':
                        output[outputPos++] = 'E';
                        break;
                    case 'Ì':
                    case 'Í':
                    case 'Î':
                    case 'Ï':
                    case 'Ĩ':
                    case 'Ī':
                    case 'Ĭ':
                    case 'Į':
                    case 'İ':
                    case 'Ɩ':
                    case 'Ɨ':
                    case 'Ǐ':
                    case 'Ȉ':
                    case 'Ȋ':
                    case 'ɪ':
                    case 'ᵻ':
                    case 'Ḭ':
                    case 'Ḯ':
                    case 'Ỉ':
                    case 'Ị':
                    case 'Ⓘ':
                    case 'ꟾ':
                    case 'Ｉ':
                        output[outputPos++] = 'I';
                        break;
                    case 'Ð':
                    case 'Ď':
                    case 'Đ':
                    case 'Ɖ':
                    case 'Ɗ':
                    case 'Ƌ':
                    case 'ᴅ':
                    case 'ᴆ':
                    case 'Ḋ':
                    case 'Ḍ':
                    case 'Ḏ':
                    case 'Ḑ':
                    case 'Ḓ':
                    case 'Ⓓ':
                    case 'Ꝺ':
                    case 'Ｄ':
                        output[outputPos++] = 'D';
                        break;
                    case 'Ñ':
                    case 'Ń':
                    case 'Ņ':
                    case 'Ň':
                    case 'Ŋ':
                    case 'Ɲ':
                    case 'Ǹ':
                    case 'Ƞ':
                    case 'ɴ':
                    case 'ᴎ':
                    case 'Ṅ':
                    case 'Ṇ':
                    case 'Ṉ':
                    case 'Ṋ':
                    case 'Ⓝ':
                    case 'Ｎ':
                        output[outputPos++] = 'N';
                        break;
                    case 'Ò':
                    case 'Ó':
                    case 'Ô':
                    case 'Õ':
                    case 'Ö':
                    case 'Ø':
                    case 'Ō':
                    case 'Ŏ':
                    case 'Ő':
                    case 'Ɔ':
                    case 'Ɵ':
                    case 'Ơ':
                    case 'Ǒ':
                    case 'Ǫ':
                    case 'Ǭ':
                    case 'Ǿ':
                    case 'Ȍ':
                    case 'Ȏ':
                    case 'Ȫ':
                    case 'Ȭ':
                    case 'Ȯ':
                    case 'Ȱ':
                    case 'ᴏ':
                    case 'ᴐ':
                    case 'Ṍ':
                    case 'Ṏ':
                    case 'Ṑ':
                    case 'Ṓ':
                    case 'Ọ':
                    case 'Ỏ':
                    case 'Ố':
                    case 'Ồ':
                    case 'Ổ':
                    case 'Ỗ':
                    case 'Ộ':
                    case 'Ớ':
                    case 'Ờ':
                    case 'Ở':
                    case 'Ỡ':
                    case 'Ợ':
                    case 'Ⓞ':
                    case 'Ꝋ':
                    case 'Ꝍ':
                    case 'Ｏ':
                        output[outputPos++] = 'O';
                        break;
                    case 'Ù':
                    case 'Ú':
                    case 'Û':
                    case 'Ü':
                    case 'Ũ':
                    case 'Ū':
                    case 'Ŭ':
                    case 'Ů':
                    case 'Ű':
                    case 'Ų':
                    case 'Ư':
                    case 'Ǔ':
                    case 'Ǖ':
                    case 'Ǘ':
                    case 'Ǚ':
                    case 'Ǜ':
                    case 'Ȕ':
                    case 'Ȗ':
                    case 'Ʉ':
                    case 'ᴜ':
                    case 'ᵾ':
                    case 'Ṳ':
                    case 'Ṵ':
                    case 'Ṷ':
                    case 'Ṹ':
                    case 'Ṻ':
                    case 'Ụ':
                    case 'Ủ':
                    case 'Ứ':
                    case 'Ừ':
                    case 'Ử':
                    case 'Ữ':
                    case 'Ự':
                    case 'Ⓤ':
                    case 'Ｕ':
                        output[outputPos++] = 'U';
                        break;
                    case 'Ý':
                    case 'Ŷ':
                    case 'Ÿ':
                    case 'Ƴ':
                    case 'Ȳ':
                    case 'Ɏ':
                    case 'ʏ':
                    case 'Ẏ':
                    case 'Ỳ':
                    case 'Ỵ':
                    case 'Ỷ':
                    case 'Ỹ':
                    case 'Ỿ':
                    case 'Ⓨ':
                    case 'Ｙ':
                        output[outputPos++] = 'Y';
                        break;
                    case 'Þ':
                    case 'Ꝧ':
                        output[outputPos++] = 'T';
                        output[outputPos++] = 'H';
                        break;
                    case 'ß':
                        output[outputPos++] = 's';
                        output[outputPos++] = 's';
                        break;
                    case 'à':
                    case 'á':
                    case 'â':
                    case 'ã':
                    case 'ä':
                    case 'å':
                    case 'ā':
                    case 'ă':
                    case 'ą':
                    case 'ǎ':
                    case 'ǟ':
                    case 'ǡ':
                    case 'ǻ':
                    case 'ȁ':
                    case 'ȃ':
                    case 'ȧ':
                    case 'ɐ':
                    case 'ə':
                    case 'ɚ':
                    case 'ᶏ':
                    case 'ᶕ':
                    case 'ḁ':
                    case 'ẚ':
                    case 'ạ':
                    case 'ả':
                    case 'ấ':
                    case 'ầ':
                    case 'ẩ':
                    case 'ẫ':
                    case 'ậ':
                    case 'ắ':
                    case 'ằ':
                    case 'ẳ':
                    case 'ẵ':
                    case 'ặ':
                    case 'ₐ':
                    case 'ₔ':
                    case 'ⓐ':
                    case 'ⱥ':
                    case 'Ɐ':
                    case 'ａ':
                        output[outputPos++] = 'a';
                        break;
                    case 'æ':
                    case 'ǣ':
                    case 'ǽ':
                    case 'ᴂ':
                        output[outputPos++] = 'a';
                        output[outputPos++] = 'e';
                        break;
                    case 'ç':
                    case 'ć':
                    case 'ĉ':
                    case 'ċ':
                    case 'č':
                    case 'ƈ':
                    case 'ȼ':
                    case 'ɕ':
                    case 'ḉ':
                    case 'ↄ':
                    case 'ⓒ':
                    case 'Ꜿ':
                    case 'ꜿ':
                    case 'ｃ':
                        output[outputPos++] = 'c';
                        break;
                    case 'è':
                    case 'é':
                    case 'ê':
                    case 'ë':
                    case 'ē':
                    case 'ĕ':
                    case 'ė':
                    case 'ę':
                    case 'ě':
                    case 'ǝ':
                    case 'ȅ':
                    case 'ȇ':
                    case 'ȩ':
                    case 'ɇ':
                    case 'ɘ':
                    case 'ɛ':
                    case 'ɜ':
                    case 'ɝ':
                    case 'ɞ':
                    case 'ʚ':
                    case 'ᴈ':
                    case 'ᶒ':
                    case 'ᶓ':
                    case 'ᶔ':
                    case 'ḕ':
                    case 'ḗ':
                    case 'ḙ':
                    case 'ḛ':
                    case 'ḝ':
                    case 'ẹ':
                    case 'ẻ':
                    case 'ẽ':
                    case 'ế':
                    case 'ề':
                    case 'ể':
                    case 'ễ':
                    case 'ệ':
                    case 'ₑ':
                    case 'ⓔ':
                    case 'ⱸ':
                    case 'ｅ':
                        output[outputPos++] = 'e';
                        break;
                    case 'ì':
                    case 'í':
                    case 'î':
                    case 'ï':
                    case 'ĩ':
                    case 'ī':
                    case 'ĭ':
                    case 'į':
                    case 'ı':
                    case 'ǐ':
                    case 'ȉ':
                    case 'ȋ':
                    case 'ɨ':
                    case 'ᴉ':
                    case 'ᵢ':
                    case 'ᵼ':
                    case 'ᶖ':
                    case 'ḭ':
                    case 'ḯ':
                    case 'ỉ':
                    case 'ị':
                    case 'ⁱ':
                    case 'ⓘ':
                    case 'ｉ':
                        output[outputPos++] = 'i';
                        break;
                    case 'ð':
                    case 'ď':
                    case 'đ':
                    case 'ƌ':
                    case 'ȡ':
                    case 'ɖ':
                    case 'ɗ':
                    case 'ᵭ':
                    case 'ᶁ':
                    case 'ᶑ':
                    case 'ḋ':
                    case 'ḍ':
                    case 'ḏ':
                    case 'ḑ':
                    case 'ḓ':
                    case 'ⓓ':
                    case 'ꝺ':
                    case 'ｄ':
                        output[outputPos++] = 'd';
                        break;
                    case 'ñ':
                    case 'ń':
                    case 'ņ':
                    case 'ň':
                    case 'ŉ':
                    case 'ŋ':
                    case 'ƞ':
                    case 'ǹ':
                    case 'ȵ':
                    case 'ɲ':
                    case 'ɳ':
                    case 'ᵰ':
                    case 'ᶇ':
                    case 'ṅ':
                    case 'ṇ':
                    case 'ṉ':
                    case 'ṋ':
                    case 'ⁿ':
                    case 'ⓝ':
                    case 'ｎ':
                        output[outputPos++] = 'n';
                        break;
                    case 'ò':
                    case 'ó':
                    case 'ô':
                    case 'õ':
                    case 'ö':
                    case 'ø':
                    case 'ō':
                    case 'ŏ':
                    case 'ő':
                    case 'ơ':
                    case 'ǒ':
                    case 'ǫ':
                    case 'ǭ':
                    case 'ǿ':
                    case 'ȍ':
                    case 'ȏ':
                    case 'ȫ':
                    case 'ȭ':
                    case 'ȯ':
                    case 'ȱ':
                    case 'ɔ':
                    case 'ɵ':
                    case 'ᴖ':
                    case 'ᴗ':
                    case 'ᶗ':
                    case 'ṍ':
                    case 'ṏ':
                    case 'ṑ':
                    case 'ṓ':
                    case 'ọ':
                    case 'ỏ':
                    case 'ố':
                    case 'ồ':
                    case 'ổ':
                    case 'ỗ':
                    case 'ộ':
                    case 'ớ':
                    case 'ờ':
                    case 'ở':
                    case 'ỡ':
                    case 'ợ':
                    case 'ₒ':
                    case 'ⓞ':
                    case 'ⱺ':
                    case 'ꝋ':
                    case 'ꝍ':
                    case 'ｏ':
                        output[outputPos++] = 'o';
                        break;
                    case 'ù':
                    case 'ú':
                    case 'û':
                    case 'ü':
                    case 'ũ':
                    case 'ū':
                    case 'ŭ':
                    case 'ů':
                    case 'ű':
                    case 'ų':
                    case 'ư':
                    case 'ǔ':
                    case 'ǖ':
                    case 'ǘ':
                    case 'ǚ':
                    case 'ǜ':
                    case 'ȕ':
                    case 'ȗ':
                    case 'ʉ':
                    case 'ᵤ':
                    case 'ᶙ':
                    case 'ṳ':
                    case 'ṵ':
                    case 'ṷ':
                    case 'ṹ':
                    case 'ṻ':
                    case 'ụ':
                    case 'ủ':
                    case 'ứ':
                    case 'ừ':
                    case 'ử':
                    case 'ữ':
                    case 'ự':
                    case 'ⓤ':
                    case 'ｕ':
                        output[outputPos++] = 'u';
                        break;
                    case 'ý':
                    case 'ÿ':
                    case 'ŷ':
                    case 'ƴ':
                    case 'ȳ':
                    case 'ɏ':
                    case 'ʎ':
                    case 'ẏ':
                    case 'ẙ':
                    case 'ỳ':
                    case 'ỵ':
                    case 'ỷ':
                    case 'ỹ':
                    case 'ỿ':
                    case 'ⓨ':
                    case 'ｙ':
                        output[outputPos++] = 'y';
                        break;
                    case 'þ':
                    case 'ᵺ':
                    case 'ꝧ':
                        output[outputPos++] = 't';
                        output[outputPos++] = 'h';
                        break;
                    case 'Ĝ':
                    case 'Ğ':
                    case 'Ġ':
                    case 'Ģ':
                    case 'Ɠ':
                    case 'Ǥ':
                    case 'ǥ':
                    case 'Ǧ':
                    case 'ǧ':
                    case 'Ǵ':
                    case 'ɢ':
                    case 'ʛ':
                    case 'Ḡ':
                    case 'Ⓖ':
                    case 'Ᵹ':
                    case 'Ꝿ':
                    case 'Ｇ':
                        output[outputPos++] = 'G';
                        break;
                    case 'ĝ':
                    case 'ğ':
                    case 'ġ':
                    case 'ģ':
                    case 'ǵ':
                    case 'ɠ':
                    case 'ɡ':
                    case 'ᵷ':
                    case 'ᵹ':
                    case 'ᶃ':
                    case 'ḡ':
                    case 'ⓖ':
                    case 'ꝿ':
                    case 'ｇ':
                        output[outputPos++] = 'g';
                        break;
                    case 'Ĥ':
                    case 'Ħ':
                    case 'Ȟ':
                    case 'ʜ':
                    case 'Ḣ':
                    case 'Ḥ':
                    case 'Ḧ':
                    case 'Ḩ':
                    case 'Ḫ':
                    case 'Ⓗ':
                    case 'Ⱨ':
                    case 'Ⱶ':
                    case 'Ｈ':
                        output[outputPos++] = 'H';
                        break;
                    case 'ĥ':
                    case 'ħ':
                    case 'ȟ':
                    case 'ɥ':
                    case 'ɦ':
                    case 'ʮ':
                    case 'ʯ':
                    case 'ḣ':
                    case 'ḥ':
                    case 'ḧ':
                    case 'ḩ':
                    case 'ḫ':
                    case 'ẖ':
                    case 'ⓗ':
                    case 'ⱨ':
                    case 'ⱶ':
                    case 'ｈ':
                        output[outputPos++] = 'h';
                        break;
                    case 'Ĳ':
                        output[outputPos++] = 'I';
                        output[outputPos++] = 'J';
                        break;
                    case 'ĳ':
                        output[outputPos++] = 'i';
                        output[outputPos++] = 'j';
                        break;
                    case 'Ĵ':
                    case 'Ɉ':
                    case 'ᴊ':
                    case 'Ⓙ':
                    case 'Ｊ':
                        output[outputPos++] = 'J';
                        break;
                    case 'ĵ':
                    case 'ǰ':
                    case 'ȷ':
                    case 'ɉ':
                    case 'ɟ':
                    case 'ʄ':
                    case 'ʝ':
                    case 'ⓙ':
                    case 'ⱼ':
                    case 'ｊ':
                        output[outputPos++] = 'j';
                        break;
                    case 'Ķ':
                    case 'Ƙ':
                    case 'Ǩ':
                    case 'ᴋ':
                    case 'Ḱ':
                    case 'Ḳ':
                    case 'Ḵ':
                    case 'Ⓚ':
                    case 'Ⱪ':
                    case 'Ꝁ':
                    case 'Ꝃ':
                    case 'Ꝅ':
                    case 'Ｋ':
                        output[outputPos++] = 'K';
                        break;
                    case 'ķ':
                    case 'ƙ':
                    case 'ǩ':
                    case 'ʞ':
                    case 'ᶄ':
                    case 'ḱ':
                    case 'ḳ':
                    case 'ḵ':
                    case 'ⓚ':
                    case 'ⱪ':
                    case 'ꝁ':
                    case 'ꝃ':
                    case 'ꝅ':
                    case 'ｋ':
                        output[outputPos++] = 'k';
                        break;
                    case 'ĸ':
                    case 'ɋ':
                    case 'ʠ':
                    case 'ⓠ':
                    case 'ꝗ':
                    case 'ꝙ':
                    case 'ｑ':
                        output[outputPos++] = 'q';
                        break;
                    case 'Ĺ':
                    case 'Ļ':
                    case 'Ľ':
                    case 'Ŀ':
                    case 'Ł':
                    case 'Ƚ':
                    case 'ʟ':
                    case 'ᴌ':
                    case 'Ḷ':
                    case 'Ḹ':
                    case 'Ḻ':
                    case 'Ḽ':
                    case 'Ⓛ':
                    case 'Ⱡ':
                    case 'Ɫ':
                    case 'Ꝇ':
                    case 'Ꝉ':
                    case 'Ꞁ':
                    case 'Ｌ':
                        output[outputPos++] = 'L';
                        break;
                    case 'ĺ':
                    case 'ļ':
                    case 'ľ':
                    case 'ŀ':
                    case 'ł':
                    case 'ƚ':
                    case 'ȴ':
                    case 'ɫ':
                    case 'ɬ':
                    case 'ɭ':
                    case 'ᶅ':
                    case 'ḷ':
                    case 'ḹ':
                    case 'ḻ':
                    case 'ḽ':
                    case 'ⓛ':
                    case 'ⱡ':
                    case 'ꝇ':
                    case 'ꝉ':
                    case 'ꞁ':
                    case 'ｌ':
                        output[outputPos++] = 'l';
                        break;
                    case 'Œ':
                    case 'ɶ':
                        output[outputPos++] = 'O';
                        output[outputPos++] = 'E';
                        break;
                    case 'œ':
                    case 'ᴔ':
                        output[outputPos++] = 'o';
                        output[outputPos++] = 'e';
                        break;
                    case 'Ŕ':
                    case 'Ŗ':
                    case 'Ř':
                    case 'Ȑ':
                    case 'Ȓ':
                    case 'Ɍ':
                    case 'ʀ':
                    case 'ʁ':
                    case 'ᴙ':
                    case 'ᴚ':
                    case 'Ṙ':
                    case 'Ṛ':
                    case 'Ṝ':
                    case 'Ṟ':
                    case 'Ⓡ':
                    case 'Ɽ':
                    case 'Ꝛ':
                    case 'Ꞃ':
                    case 'Ｒ':
                        output[outputPos++] = 'R';
                        break;
                    case 'ŕ':
                    case 'ŗ':
                    case 'ř':
                    case 'ȑ':
                    case 'ȓ':
                    case 'ɍ':
                    case 'ɼ':
                    case 'ɽ':
                    case 'ɾ':
                    case 'ɿ':
                    case 'ᵣ':
                    case 'ᵲ':
                    case 'ᵳ':
                    case 'ᶉ':
                    case 'ṙ':
                    case 'ṛ':
                    case 'ṝ':
                    case 'ṟ':
                    case 'ⓡ':
                    case 'ꝛ':
                    case 'ꞃ':
                    case 'ｒ':
                        output[outputPos++] = 'r';
                        break;
                    case 'Ś':
                    case 'Ŝ':
                    case 'Ş':
                    case 'Š':
                    case 'Ș':
                    case 'Ṡ':
                    case 'Ṣ':
                    case 'Ṥ':
                    case 'Ṧ':
                    case 'Ṩ':
                    case 'Ⓢ':
                    case 'ꜱ':
                    case 'ꞅ':
                    case 'Ｓ':
                        output[outputPos++] = 'S';
                        break;
                    case 'ś':
                    case 'ŝ':
                    case 'ş':
                    case 'š':
                    case 'ſ':
                    case 'ș':
                    case 'ȿ':
                    case 'ʂ':
                    case 'ᵴ':
                    case 'ᶊ':
                    case 'ṡ':
                    case 'ṣ':
                    case 'ṥ':
                    case 'ṧ':
                    case 'ṩ':
                    case 'ẜ':
                    case 'ẝ':
                    case 'ⓢ':
                    case 'Ꞅ':
                    case 'ｓ':
                        output[outputPos++] = 's';
                        break;
                    case 'Ţ':
                    case 'Ť':
                    case 'Ŧ':
                    case 'Ƭ':
                    case 'Ʈ':
                    case 'Ț':
                    case 'Ⱦ':
                    case 'ᴛ':
                    case 'Ṫ':
                    case 'Ṭ':
                    case 'Ṯ':
                    case 'Ṱ':
                    case 'Ⓣ':
                    case 'Ꞇ':
                    case 'Ｔ':
                        output[outputPos++] = 'T';
                        break;
                    case 'ţ':
                    case 'ť':
                    case 'ŧ':
                    case 'ƫ':
                    case 'ƭ':
                    case 'ț':
                    case 'ȶ':
                    case 'ʇ':
                    case 'ʈ':
                    case 'ᵵ':
                    case 'ṫ':
                    case 'ṭ':
                    case 'ṯ':
                    case 'ṱ':
                    case 'ẗ':
                    case 'ⓣ':
                    case 'ⱦ':
                    case 'ｔ':
                        output[outputPos++] = 't';
                        break;
                    case 'Ŵ':
                    case 'Ƿ':
                    case 'ᴡ':
                    case 'Ẁ':
                    case 'Ẃ':
                    case 'Ẅ':
                    case 'Ẇ':
                    case 'Ẉ':
                    case 'Ⓦ':
                    case 'Ⱳ':
                    case 'Ｗ':
                        output[outputPos++] = 'W';
                        break;
                    case 'ŵ':
                    case 'ƿ':
                    case 'ʍ':
                    case 'ẁ':
                    case 'ẃ':
                    case 'ẅ':
                    case 'ẇ':
                    case 'ẉ':
                    case 'ẘ':
                    case 'ⓦ':
                    case 'ⱳ':
                    case 'ｗ':
                        output[outputPos++] = 'w';
                        break;
                    case 'Ź':
                    case 'Ż':
                    case 'Ž':
                    case 'Ƶ':
                    case 'Ȝ':
                    case 'Ȥ':
                    case 'ᴢ':
                    case 'Ẑ':
                    case 'Ẓ':
                    case 'Ẕ':
                    case 'Ⓩ':
                    case 'Ⱬ':
                    case 'Ꝣ':
                    case 'Ｚ':
                        output[outputPos++] = 'Z';
                        break;
                    case 'ź':
                    case 'ż':
                    case 'ž':
                    case 'ƶ':
                    case 'ȝ':
                    case 'ȥ':
                    case 'ɀ':
                    case 'ʐ':
                    case 'ʑ':
                    case 'ᵶ':
                    case 'ᶎ':
                    case 'ẑ':
                    case 'ẓ':
                    case 'ẕ':
                    case 'ⓩ':
                    case 'ⱬ':
                    case 'ꝣ':
                    case 'ｚ':
                        output[outputPos++] = 'z';
                        break;
                    case 'ƀ':
                    case 'ƃ':
                    case 'ɓ':
                    case 'ᵬ':
                    case 'ᶀ':
                    case 'ḃ':
                    case 'ḅ':
                    case 'ḇ':
                    case 'ⓑ':
                    case 'ｂ':
                        output[outputPos++] = 'b';
                        break;
                    case 'Ɓ':
                    case 'Ƃ':
                    case 'Ƀ':
                    case 'ʙ':
                    case 'ᴃ':
                    case 'Ḃ':
                    case 'Ḅ':
                    case 'Ḇ':
                    case 'Ⓑ':
                    case 'Ｂ':
                        output[outputPos++] = 'B';
                        break;
                    case 'Ƒ':
                    case 'Ḟ':
                    case 'Ⓕ':
                    case 'ꜰ':
                    case 'Ꝼ':
                    case 'ꟻ':
                    case 'Ｆ':
                        output[outputPos++] = 'F';
                        break;
                    case 'ƒ':
                    case 'ᵮ':
                    case 'ᶂ':
                    case 'ḟ':
                    case 'ẛ':
                    case 'ⓕ':
                    case 'ꝼ':
                    case 'ｆ':
                        output[outputPos++] = 'f';
                        break;
                    case 'ƕ':
                        output[outputPos++] = 'h';
                        output[outputPos++] = 'v';
                        break;
                    case 'Ɯ':
                    case 'ᴍ':
                    case 'Ḿ':
                    case 'Ṁ':
                    case 'Ṃ':
                    case 'Ⓜ':
                    case 'Ɱ':
                    case 'ꟽ':
                    case 'ꟿ':
                    case 'Ｍ':
                        output[outputPos++] = 'M';
                        break;
                    case 'Ƥ':
                    case 'ᴘ':
                    case 'Ṕ':
                    case 'Ṗ':
                    case 'Ⓟ':
                    case 'Ᵽ':
                    case 'Ꝑ':
                    case 'Ꝓ':
                    case 'Ꝕ':
                    case 'Ｐ':
                        output[outputPos++] = 'P';
                        break;
                    case 'ƥ':
                    case 'ᵱ':
                    case 'ᵽ':
                    case 'ᶈ':
                    case 'ṕ':
                    case 'ṗ':
                    case 'ⓟ':
                    case 'ꝑ':
                    case 'ꝓ':
                    case 'ꝕ':
                    case 'ꟼ':
                    case 'ｐ':
                        output[outputPos++] = 'p';
                        break;
                    case 'Ʋ':
                    case 'Ʌ':
                    case 'ᴠ':
                    case 'Ṽ':
                    case 'Ṿ':
                    case 'Ỽ':
                    case 'Ⓥ':
                    case 'Ꝟ':
                    case 'Ꝩ':
                    case 'Ｖ':
                        output[outputPos++] = 'V';
                        break;
                    case 'Ǆ':
                    case 'Ǳ':
                        output[outputPos++] = 'D';
                        output[outputPos++] = 'Z';
                        break;
                    case 'ǅ':
                    case 'ǲ':
                        output[outputPos++] = 'D';
                        output[outputPos++] = 'z';
                        break;
                    case 'ǆ':
                    case 'ǳ':
                    case 'ʣ':
                    case 'ʥ':
                        output[outputPos++] = 'd';
                        output[outputPos++] = 'z';
                        break;
                    case 'Ǉ':
                        output[outputPos++] = 'L';
                        output[outputPos++] = 'J';
                        break;
                    case 'ǈ':
                        output[outputPos++] = 'L';
                        output[outputPos++] = 'j';
                        break;
                    case 'ǉ':
                        output[outputPos++] = 'l';
                        output[outputPos++] = 'j';
                        break;
                    case 'Ǌ':
                        output[outputPos++] = 'N';
                        output[outputPos++] = 'J';
                        break;
                    case 'ǋ':
                        output[outputPos++] = 'N';
                        output[outputPos++] = 'j';
                        break;
                    case 'ǌ':
                        output[outputPos++] = 'n';
                        output[outputPos++] = 'j';
                        break;
                    case 'Ƕ':
                        output[outputPos++] = 'H';
                        output[outputPos++] = 'V';
                        break;
                    case 'Ȣ':
                    case 'ᴕ':
                        output[outputPos++] = 'O';
                        output[outputPos++] = 'U';
                        break;
                    case 'ȣ':
                        output[outputPos++] = 'o';
                        output[outputPos++] = 'u';
                        break;
                    case 'ȸ':
                        output[outputPos++] = 'd';
                        output[outputPos++] = 'b';
                        break;
                    case 'ȹ':
                        output[outputPos++] = 'q';
                        output[outputPos++] = 'p';
                        break;
                    case 'Ɋ':
                    case 'Ⓠ':
                    case 'Ꝗ':
                    case 'Ꝙ':
                    case 'Ｑ':
                        output[outputPos++] = 'Q';
                        break;
                    case 'ɯ':
                    case 'ɰ':
                    case 'ɱ':
                    case 'ᵯ':
                    case 'ᶆ':
                    case 'ḿ':
                    case 'ṁ':
                    case 'ṃ':
                    case 'ⓜ':
                    case 'ｍ':
                        output[outputPos++] = 'm';
                        break;
                    case 'ʋ':
                    case 'ʌ':
                    case 'ᵥ':
                    case 'ᶌ':
                    case 'ṽ':
                    case 'ṿ':
                    case 'ⓥ':
                    case 'ⱱ':
                    case 'ⱴ':
                    case 'ꝟ':
                    case 'ｖ':
                        output[outputPos++] = 'v';
                        break;
                    case 'ʦ':
                        output[outputPos++] = 't';
                        output[outputPos++] = 's';
                        break;
                    case 'ʨ':
                        output[outputPos++] = 't';
                        output[outputPos++] = 'c';
                        break;
                    case 'ʪ':
                        output[outputPos++] = 'l';
                        output[outputPos++] = 's';
                        break;
                    case 'ʫ':
                        output[outputPos++] = 'l';
                        output[outputPos++] = 'z';
                        break;
                    case 'ᵫ':
                        output[outputPos++] = 'u';
                        output[outputPos++] = 'e';
                        break;
                    case 'ᶍ':
                    case 'ẋ':
                    case 'ẍ':
                    case 'ₓ':
                    case 'ⓧ':
                    case 'ｘ':
                        output[outputPos++] = 'x';
                        break;
                    case 'Ẋ':
                    case 'Ẍ':
                    case 'Ⓧ':
                    case 'Ｘ':
                        output[outputPos++] = 'X';
                        break;
                    case 'ẞ':
                        output[outputPos++] = 'S';
                        output[outputPos++] = 'S';
                        break;
                    case 'Ỻ':
                        output[outputPos++] = 'L';
                        output[outputPos++] = 'L';
                        break;
                    case 'ỻ':
                        output[outputPos++] = 'l';
                        output[outputPos++] = 'l';
                        break;
                    case '‐':
                    case '‑':
                    case '‒':
                    case '–':
                    case '—':
                    case '⁻':
                    case '₋':
                    case '－':
                        output[outputPos++] = '-';
                        break;
                    case '‘':
                    case '’':
                    case '‚':
                    case '‛':
                    case '′':
                    case '‵':
                    case '‹':
                    case '›':
                    case '❛':
                    case '❜':
                    case '＇':
                        output[outputPos++] = '\'';
                        break;
                    case '‸':
                    case '＾':
                        output[outputPos++] = '^';
                        break;
                    case '‼':
                        output[outputPos++] = '!';
                        output[outputPos++] = '!';
                        break;
                    case '⁄':
                    case '／':
                        output[outputPos++] = '/';
                        break;
                    case '⁅':
                    case '❲':
                    case '［':
                        output[outputPos++] = '[';
                        break;
                    case '⁆':
                    case '❳':
                    case '］':
                        output[outputPos++] = ']';
                        break;
                    case '⁇':
                        output[outputPos++] = '?';
                        output[outputPos++] = '?';
                        break;
                    case '⁈':
                        output[outputPos++] = '?';
                        output[outputPos++] = '!';
                        break;
                    case '⁉':
                        output[outputPos++] = '!';
                        output[outputPos++] = '?';
                        break;
                    case '⁎':
                    case '＊':
                        output[outputPos++] = '*';
                        break;
                    case '⁏':
                    case '；':
                        output[outputPos++] = ';';
                        break;
                    case '⁒':
                    case '％':
                        output[outputPos++] = '%';
                        break;
                    case '⁓':
                    case '～':
                        output[outputPos++] = '~';
                        break;
                    case '⁰':
                    case '₀':
                    case '⓪':
                    case '⓿':
                    case '０':
                        output[outputPos++] = '0';
                        break;
                    case '⁴':
                    case '₄':
                    case '④':
                    case '⓸':
                    case '❹':
                    case '➃':
                    case '➍':
                    case '４':
                        output[outputPos++] = '4';
                        break;
                    case '⁵':
                    case '₅':
                    case '⑤':
                    case '⓹':
                    case '❺':
                    case '➄':
                    case '➎':
                    case '５':
                        output[outputPos++] = '5';
                        break;
                    case '⁶':
                    case '₆':
                    case '⑥':
                    case '⓺':
                    case '❻':
                    case '➅':
                    case '➏':
                    case '６':
                        output[outputPos++] = '6';
                        break;
                    case '⁷':
                    case '₇':
                    case '⑦':
                    case '⓻':
                    case '❼':
                    case '➆':
                    case '➐':
                    case '７':
                        output[outputPos++] = '7';
                        break;
                    case '⁸':
                    case '₈':
                    case '⑧':
                    case '⓼':
                    case '❽':
                    case '➇':
                    case '➑':
                    case '８':
                        output[outputPos++] = '8';
                        break;
                    case '⁹':
                    case '₉':
                    case '⑨':
                    case '⓽':
                    case '❾':
                    case '➈':
                    case '➒':
                    case '９':
                        output[outputPos++] = '9';
                        break;
                    case '⁺':
                    case '₊':
                    case '＋':
                        output[outputPos++] = '+';
                        break;
                    case '⁼':
                    case '₌':
                    case '＝':
                        output[outputPos++] = '=';
                        break;
                    case '⁽':
                    case '₍':
                    case '❨':
                    case '❪':
                    case '（':
                        output[outputPos++] = '(';
                        break;
                    case '⁾':
                    case '₎':
                    case '❩':
                    case '❫':
                    case '）':
                        output[outputPos++] = ')';
                        break;
                    case '⑩':
                    case '⓾':
                    case '❿':
                    case '➉':
                    case '➓':
                        output[outputPos++] = '1';
                        output[outputPos++] = '0';
                        break;
                    case '⑪':
                    case '⓫':
                        output[outputPos++] = '1';
                        output[outputPos++] = '1';
                        break;
                    case '⑫':
                    case '⓬':
                        output[outputPos++] = '1';
                        output[outputPos++] = '2';
                        break;
                    case '⑬':
                    case '⓭':
                        output[outputPos++] = '1';
                        output[outputPos++] = '3';
                        break;
                    case '⑭':
                    case '⓮':
                        output[outputPos++] = '1';
                        output[outputPos++] = '4';
                        break;
                    case '⑮':
                    case '⓯':
                        output[outputPos++] = '1';
                        output[outputPos++] = '5';
                        break;
                    case '⑯':
                    case '⓰':
                        output[outputPos++] = '1';
                        output[outputPos++] = '6';
                        break;
                    case '⑰':
                    case '⓱':
                        output[outputPos++] = '1';
                        output[outputPos++] = '7';
                        break;
                    case '⑱':
                    case '⓲':
                        output[outputPos++] = '1';
                        output[outputPos++] = '8';
                        break;
                    case '⑲':
                    case '⓳':
                        output[outputPos++] = '1';
                        output[outputPos++] = '9';
                        break;
                    case '⑳':
                    case '⓴':
                        output[outputPos++] = '2';
                        output[outputPos++] = '0';
                        break;
                    case '⑴':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = ')';
                        break;
                    case '⑵':
                        output[outputPos++] = '(';
                        output[outputPos++] = '2';
                        output[outputPos++] = ')';
                        break;
                    case '⑶':
                        output[outputPos++] = '(';
                        output[outputPos++] = '3';
                        output[outputPos++] = ')';
                        break;
                    case '⑷':
                        output[outputPos++] = '(';
                        output[outputPos++] = '4';
                        output[outputPos++] = ')';
                        break;
                    case '⑸':
                        output[outputPos++] = '(';
                        output[outputPos++] = '5';
                        output[outputPos++] = ')';
                        break;
                    case '⑹':
                        output[outputPos++] = '(';
                        output[outputPos++] = '6';
                        output[outputPos++] = ')';
                        break;
                    case '⑺':
                        output[outputPos++] = '(';
                        output[outputPos++] = '7';
                        output[outputPos++] = ')';
                        break;
                    case '⑻':
                        output[outputPos++] = '(';
                        output[outputPos++] = '8';
                        output[outputPos++] = ')';
                        break;
                    case '⑼':
                        output[outputPos++] = '(';
                        output[outputPos++] = '9';
                        output[outputPos++] = ')';
                        break;
                    case '⑽':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '0';
                        output[outputPos++] = ')';
                        break;
                    case '⑾':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '1';
                        output[outputPos++] = ')';
                        break;
                    case '⑿':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '2';
                        output[outputPos++] = ')';
                        break;
                    case '⒀':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '3';
                        output[outputPos++] = ')';
                        break;
                    case '⒁':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '4';
                        output[outputPos++] = ')';
                        break;
                    case '⒂':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '5';
                        output[outputPos++] = ')';
                        break;
                    case '⒃':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '6';
                        output[outputPos++] = ')';
                        break;
                    case '⒄':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '7';
                        output[outputPos++] = ')';
                        break;
                    case '⒅':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '8';
                        output[outputPos++] = ')';
                        break;
                    case '⒆':
                        output[outputPos++] = '(';
                        output[outputPos++] = '1';
                        output[outputPos++] = '9';
                        output[outputPos++] = ')';
                        break;
                    case '⒇':
                        output[outputPos++] = '(';
                        output[outputPos++] = '2';
                        output[outputPos++] = '0';
                        output[outputPos++] = ')';
                        break;
                    case '⒈':
                        output[outputPos++] = '1';
                        output[outputPos++] = '.';
                        break;
                    case '⒉':
                        output[outputPos++] = '2';
                        output[outputPos++] = '.';
                        break;
                    case '⒊':
                        output[outputPos++] = '3';
                        output[outputPos++] = '.';
                        break;
                    case '⒋':
                        output[outputPos++] = '4';
                        output[outputPos++] = '.';
                        break;
                    case '⒌':
                        output[outputPos++] = '5';
                        output[outputPos++] = '.';
                        break;
                    case '⒍':
                        output[outputPos++] = '6';
                        output[outputPos++] = '.';
                        break;
                    case '⒎':
                        output[outputPos++] = '7';
                        output[outputPos++] = '.';
                        break;
                    case '⒏':
                        output[outputPos++] = '8';
                        output[outputPos++] = '.';
                        break;
                    case '⒐':
                        output[outputPos++] = '9';
                        output[outputPos++] = '.';
                        break;
                    case '⒑':
                        output[outputPos++] = '1';
                        output[outputPos++] = '0';
                        output[outputPos++] = '.';
                        break;
                    case '⒒':
                        output[outputPos++] = '1';
                        output[outputPos++] = '1';
                        output[outputPos++] = '.';
                        break;
                    case '⒓':
                        output[outputPos++] = '1';
                        output[outputPos++] = '2';
                        output[outputPos++] = '.';
                        break;
                    case '⒔':
                        output[outputPos++] = '1';
                        output[outputPos++] = '3';
                        output[outputPos++] = '.';
                        break;
                    case '⒕':
                        output[outputPos++] = '1';
                        output[outputPos++] = '4';
                        output[outputPos++] = '.';
                        break;
                    case '⒖':
                        output[outputPos++] = '1';
                        output[outputPos++] = '5';
                        output[outputPos++] = '.';
                        break;
                    case '⒗':
                        output[outputPos++] = '1';
                        output[outputPos++] = '6';
                        output[outputPos++] = '.';
                        break;
                    case '⒘':
                        output[outputPos++] = '1';
                        output[outputPos++] = '7';
                        output[outputPos++] = '.';
                        break;
                    case '⒙':
                        output[outputPos++] = '1';
                        output[outputPos++] = '8';
                        output[outputPos++] = '.';
                        break;
                    case '⒚':
                        output[outputPos++] = '1';
                        output[outputPos++] = '9';
                        output[outputPos++] = '.';
                        break;
                    case '⒛':
                        output[outputPos++] = '2';
                        output[outputPos++] = '0';
                        output[outputPos++] = '.';
                        break;
                    case '⒜':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'a';
                        output[outputPos++] = ')';
                        break;
                    case '⒝':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'b';
                        output[outputPos++] = ')';
                        break;
                    case '⒞':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'c';
                        output[outputPos++] = ')';
                        break;
                    case '⒟':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'd';
                        output[outputPos++] = ')';
                        break;
                    case '⒠':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'e';
                        output[outputPos++] = ')';
                        break;
                    case '⒡':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'f';
                        output[outputPos++] = ')';
                        break;
                    case '⒢':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'g';
                        output[outputPos++] = ')';
                        break;
                    case '⒣':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'h';
                        output[outputPos++] = ')';
                        break;
                    case '⒤':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'i';
                        output[outputPos++] = ')';
                        break;
                    case '⒥':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'j';
                        output[outputPos++] = ')';
                        break;
                    case '⒦':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'k';
                        output[outputPos++] = ')';
                        break;
                    case '⒧':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'l';
                        output[outputPos++] = ')';
                        break;
                    case '⒨':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'm';
                        output[outputPos++] = ')';
                        break;
                    case '⒩':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'n';
                        output[outputPos++] = ')';
                        break;
                    case '⒪':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'o';
                        output[outputPos++] = ')';
                        break;
                    case '⒫':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'p';
                        output[outputPos++] = ')';
                        break;
                    case '⒬':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'q';
                        output[outputPos++] = ')';
                        break;
                    case '⒭':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'r';
                        output[outputPos++] = ')';
                        break;
                    case '⒮':
                        output[outputPos++] = '(';
                        output[outputPos++] = 's';
                        output[outputPos++] = ')';
                        break;
                    case '⒯':
                        output[outputPos++] = '(';
                        output[outputPos++] = 't';
                        output[outputPos++] = ')';
                        break;
                    case '⒰':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'u';
                        output[outputPos++] = ')';
                        break;
                    case '⒱':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'v';
                        output[outputPos++] = ')';
                        break;
                    case '⒲':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'w';
                        output[outputPos++] = ')';
                        break;
                    case '⒳':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'x';
                        output[outputPos++] = ')';
                        break;
                    case '⒴':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'y';
                        output[outputPos++] = ')';
                        break;
                    case '⒵':
                        output[outputPos++] = '(';
                        output[outputPos++] = 'z';
                        output[outputPos++] = ')';
                        break;
                    case '❬':
                    case '❰':
                    case '＜':
                        output[outputPos++] = '<';
                        break;
                    case '❭':
                    case '❱':
                    case '＞':
                        output[outputPos++] = '>';
                        break;
                    case '❴':
                    case '｛':
                        output[outputPos++] = '{';
                        break;
                    case '❵':
                    case '｝':
                        output[outputPos++] = '}';
                        break;
                    case '⸨':
                        output[outputPos++] = '(';
                        output[outputPos++] = '(';
                        break;
                    case '⸩':
                        output[outputPos++] = ')';
                        output[outputPos++] = ')';
                        break;
                    case 'Ꜩ':
                        output[outputPos++] = 'T';
                        output[outputPos++] = 'Z';
                        break;
                    case 'ꜩ':
                        output[outputPos++] = 't';
                        output[outputPos++] = 'z';
                        break;
                    case 'Ꜳ':
                        output[outputPos++] = 'A';
                        output[outputPos++] = 'A';
                        break;
                    case 'ꜳ':
                        output[outputPos++] = 'a';
                        output[outputPos++] = 'a';
                        break;
                    case 'Ꜵ':
                        output[outputPos++] = 'A';
                        output[outputPos++] = 'O';
                        break;
                    case 'ꜵ':
                        output[outputPos++] = 'a';
                        output[outputPos++] = 'o';
                        break;
                    case 'Ꜷ':
                        output[outputPos++] = 'A';
                        output[outputPos++] = 'U';
                        break;
                    case 'ꜷ':
                        output[outputPos++] = 'a';
                        output[outputPos++] = 'u';
                        break;
                    case 'Ꜹ':
                    case 'Ꜻ':
                        output[outputPos++] = 'A';
                        output[outputPos++] = 'V';
                        break;
                    case 'ꜹ':
                    case 'ꜻ':
                        output[outputPos++] = 'a';
                        output[outputPos++] = 'v';
                        break;
                    case 'Ꜽ':
                        output[outputPos++] = 'A';
                        output[outputPos++] = 'Y';
                        break;
                    case 'ꜽ':
                        output[outputPos++] = 'a';
                        output[outputPos++] = 'y';
                        break;
                    case 'Ꝏ':
                        output[outputPos++] = 'O';
                        output[outputPos++] = 'O';
                        break;
                    case 'ꝏ':
                        output[outputPos++] = 'o';
                        output[outputPos++] = 'o';
                        break;
                    case 'Ꝡ':
                        output[outputPos++] = 'V';
                        output[outputPos++] = 'Y';
                        break;
                    case 'ꝡ':
                        output[outputPos++] = 'v';
                        output[outputPos++] = 'y';
                        break;
                    case 'ﬀ':
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'f';
                        break;
                    case 'ﬁ':
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'i';
                        break;
                    case 'ﬂ':
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'l';
                        break;
                    case 'ﬃ':
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'i';
                        break;
                    case 'ﬄ':
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'f';
                        output[outputPos++] = 'l';
                        break;
                    case 'ﬆ':
                        output[outputPos++] = 's';
                        output[outputPos++] = 't';
                        break;
                    case '！':
                        output[outputPos++] = '!';
                        break;
                    case '＃':
                        output[outputPos++] = '#';
                        break;
                    case '＄':
                        output[outputPos++] = '$';
                        break;
                    case '＆':
                        output[outputPos++] = '&';
                        break;
                    case '，':
                        output[outputPos++] = ',';
                        break;
                    case '．':
                        output[outputPos++] = '.';
                        break;
                    case '：':
                        output[outputPos++] = ':';
                        break;
                    case '？':
                        output[outputPos++] = '?';
                        break;
                    case '＠':
                        output[outputPos++] = '@';
                        break;
                    case '＼':
                        output[outputPos++] = '\\';
                        break;
                    case '＿':
                        output[outputPos++] = '_';
                        break;
                    default:
                        output[outputPos++] = c;
                }
            }
        }

        return outputPos;
    }
}
