package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换的字符
    private static final String REPLACEMENT = "***";

    //根结点
    private TrieNode rootNode = new TrieNode();

    //初始化的方法
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while ((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }

    }

    //将敏感词添加到前缀树中
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指向子节点，进行下次循环
            tempNode = subNode;

            //最后一个字符要有结束标识
            if (i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }

        }
    }


    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return  过滤后的文本
     */
    public String filter(String text){
        //内容是否空
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果集
        StringBuilder sb = new StringBuilder();

        //遍历文本
        while(position < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根结点,将此符号记录到结果集，指针2后移
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //但是不管符号在开头还是中间位置，指针3一定会后移
                position++;
                continue;
            }

            //检查下级结点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //指针重新指向根结点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd){
                //发现敏感词，将begin到position之间的字符串替换
                sb.append(REPLACEMENT);
                //position进入下一个位置
                begin = ++ position;
            }else {
                //没有检测完。后面没有敏感词。继续检测
                position++;
            }

        }

        //将最后一批字符加入结果集
        sb.append(text.substring(begin));

        return sb.toString();

    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80 ~ 0x9FFF 是东亚文字符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义一个前缀树
    private class TrieNode{

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 当前结点的子节点(key是下级字符，value是下级结点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点的方法
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }

}
