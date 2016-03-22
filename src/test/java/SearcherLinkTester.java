import org.junit.Test;
import service.searcherLinks.SearcherLink;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class SearcherLinkTester {

    @Test
    public void searcherLinkTest1() {
        String text = "href=\"http://skillbox.io\" sdfdghdfgdfhfdghsdgjdlfkjglkdfjgldfjlgkljdfgkjldfgkllhref=\"http://ru.wikipedia.org/wiki/%D0%A0%D0%B5%D0%B3%D1%83%D0%BB%D1%8F%D1%80%D0%BD%D1%8B%D0%B5_%D0%B2%D1%8B%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F\"";
        String firstAnswer = "http://skillbox.io";
        String secondAnswer = "http://ru.wikipedia.org/wiki/%D0%A0%D0%B5%D0%B3%D1%83%D0%BB%D1%8F%D1%80%D0%BD%D1%8B%D0%B5_%D0%B2%D1%8B%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F";
        List<String> answer = SearcherLink.getLinks(text);
        answer.forEach(System.out::println);
        assertEquals(2, answer.size());
        assertEquals( firstAnswer,answer.get(0));
        assertEquals(secondAnswer,answer.get(1));
    }

    @Test
    public void searcherLinkTest2() {
        String text = "This lesson covers the Java platform classes used for basic I/O. It first focuses on I/O Streams, a powerful concept that greatly simplifies I/O operations. The lesson also looks at serialization, which lets a program write whole objects out to streams and read them back again. Then the lesson looks at file I/O and file system operations, including random access files.\n" +
                "\n" +
                "Most of the classes covered in the I/O Streams section are in the java.io package. Most of the classes covered in the File I/O section are in the java.nio.file package.\n" +
                "I/O Streams\n" +
                "\n" +
                "    Byte Streams handle I/O of raw binary data.\n" +
                "    Character Streams handle I/O of character data, automatically handling translation to and from the local character set.\n" +
                "    Buffered Streams optimize input and output by reducing the number of calls to the native API.\n" +
                "    Scanning and Formatting allows a program to read and write formatted text.\n" +
                "    I/O from the Command Line describes the Standard Streams and the Console object.\n" +
                "    Data Streams handle binary I/O of primitive data type and String values.\n" +
                "    Object Streams handle binary I/O of objects.\n" +
                "\n" +
                "File I/O (Featuring NIO.2)\n" +
                "\n" +
                "    What is a Path? examines the concept of a path on a file system.\n" +
                "    The Path Class introduces the cornerstone class of the java.nio.file package.\n" +
                "    Path Operations looks at methods in the Path class that deal with syntactic operations.\n" +
                "    File Operations introduces concepts common to many of the file I/O methods.\n" +
                "    Checking a File or Directory shows how to check a file's existence and its level of accessibility.\n" +
                "    Deleting a File or Directory.\n" +
                "    Copying a File or Directory.\n" +
                "    Moving a File or Directory.\n" +
                "    Managing Metadata explains how to read and set file attributes.\n" +
                "    Reading, Writing and Creating Files shows the stream and channel methods for reading and writing files.\n" +
                "    Random Access Files shows how to read or write files in a non-sequentially manner.\n" +
                "    Creating and Reading Directories covers API specific to directories, such as how to list a directory's contents.\n" +
                "    Links, Symbolic or Otherwise covers issues specific to symbolic and hard links.\n" +
                "    Walking the File Tree demonstrates how to recursively visit each file and directory in a file tree.\n" +
                "    Finding Files shows how to search for files using pattern matching.\n" +
                "    Watching a Directory for Changes shows how to use the watch service to detect files that are added, removed or updated in one or more directories.\n" +
                "    Other Useful Methods covers important API that didn't fit elsewhere in the lesson.\n" +
                "    Legacy File I/O Code shows how to leverage Path functionality if you have older code using the java.io.File class. A table mapping java.io.File API to java.nio.file API is provided.\n";
        List<String> answer = SearcherLink.getLinks(text);
        assertEquals(0, answer.size());
    }

    @Test
    public void searcherLinkTest3() {
        String text = "<a href=\"/vanya/gladush.com\" and some one and what  to configure the behavior when a symbolic link is encountered. href=\"/vanya/znaet/chto.reshaet%300%324\"";
        text += "\n\n\ntext and more text \t<bt><sdf><ahref=\"/igladush.com\"";
        String firstAnswer = "/vanya/gladush.com";
        String secondAnswer = "/vanya/znaet/chto.reshaet%300%324";
        String thirdAnswer = "/igladush.com";
        List<String> answer = SearcherLink.getLinks(text);
        answer.forEach(System.out::println);
        assertEquals(3, answer.size());

        assertEquals(firstAnswer, answer.get(0));
        assertEquals(secondAnswer, answer.get(1));
        assertEquals(thirdAnswer, answer.get(2));

    }

    @Test
    public void searcherLinkTest4() {

        String text = "<a href=\"http://www.quizful.net/test/java_se_basic\">some text</>";
        String firstAnswer = "http://www.quizful.net/test/java_se_basic";
        List<String> answer = SearcherLink.getLinks(text);
        answer.forEach(System.out::println);
        assertEquals(1, answer.size());
        assertEquals(firstAnswer,answer.get(0));
    }


    @Test
    public void searchLinkWithoutAnchorLinkTest1(){
        String text=" URL, возможно, добавил к этому \"фрагмент\", также " +
                "известный как \"касательно\" или \"ссылка\". Фрагмент обозначается резким символом знака" +
                " \"#\" сопровождаемый большим количеством символов. Например,\n" +
                "   href=\"http://java.sun.com/index.html#chapter1\n";
        String firstAnswer="http://java.sun.com/index.html";
        List<String> answer=SearcherLink.getLinks(text);
        assertEquals(1,answer.size());
        assertEquals(true,firstAnswer.equals(answer.get(0)));

    }

    @Test
    public void searchLinkWithoutAnchorLinkTest2(){
        String text="URL является акронимом для Универсального Локатора Ресурса." +
                " Это - ссылка (адрес) к ресурсу в Интернете. Вы обеспечивае"+
                "href=\"http://spec-zone.ru/RU/Java/Tutorials#networking#urls#index.html\""+
                " же образом, что Вы обеспечиваете адреса на буквах так, чтобы почтовое отделение могло определить"+
                "href=\"http://docs.oracle.com/javase/6/docs#api#java%URL.html\"";
        String firstAnswer="http://spec-zone.ru/RU/Java/Tutorials";
        String secondAnswer="http://docs.oracle.com/javase/6/docs";
        List<String> answer=SearcherLink.getLinks(text);
        assertEquals(2,answer.size());
        assertEquals(firstAnswer,answer.get(0));
        assertEquals(secondAnswer,answer.get(1));
    }

    @Test
    public void searchLinkWithoutParameter1(){
        String text="sdfghsdfk j sdfjk ghsd; e4 ish;dfjgh;sjdf h;eir 'sdfg 'eirghdfkjgh dfgh;sdfjgh;sdk j href=\"https://en.wikipedia.org/w/index.php?title=Freenom&amp;action=edit&amp;redlink=1\"jsdfh;gkjsdfh;gkjhsd;fkjb dfghsdjkf sd;fg" +
                "sdfgds;f ksdfh sdf'sd f' sdfgh sd;kfgh ksdf;sdfg ;se rdgh";
        String firstAnswer="https://en.wikipedia.org/w/index.php?title=Freenom";
        List<String> answer=SearcherLink.getLinks(text);
        assertEquals(1,answer.size());
        assertEquals(firstAnswer,answer.get(0));
    }
    @Test
    public void searchLinkWithoutParameter2(){
        String text=" href=\"/w/load.php?debug=false&amp;lang=en&amp;modules=site&amp;only=styles&amp;skin=vector\"";
        String firstAnswer="/w/load.php?debug=false";
        List<String> answer= SearcherLink.getLinks(text);
        assertEquals(1,answer.size());
        assertEquals(firstAnswer,answer.get(0));

    }



}
