package com.wordle.wordlegame;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordLoader {

    private List<String> wordList;
    private String secretWord;


    public WordLoader() {
        loadWordXML();
        secretWord = loadRandomWord();
    }

    public String getSecretWord() {
        return secretWord;
    }
    private void loadWordXML() {
        wordList = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(getClass().getResourceAsStream("/words.xml"));
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("word");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    wordList.add(element.getTextContent().toUpperCase().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadRandomWord() {
        if (wordList == null || wordList.isEmpty()) {
            return "fallback";
        }
        Random random = new Random();
        return wordList.get(random.nextInt(wordList.size()));
    }
}

