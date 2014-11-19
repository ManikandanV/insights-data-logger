package org.ednovo.data.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "question")
public class QuestionCo {

	@Column
	private String question;

	@Column
	private String type;

	@Column
	private String explanation;

	@Column
	private String timeToCompleteInSec;

	@Column
	private String gc;

	@Column
	private String concept;

	@Column
	private String source;

	@Column
	private String answer;

	@Column
	private String hint;

	@Column
	private Integer answerOptionCount;

	@Column
	private Integer hintCount;

	@Column
	private String importCode;

	@Column
	private Set<String> quizGooruOIds;

	@Column
	private String answerTexts;

	@Column
	private String hintTexts;
	
	@Column
	private String explanationAsset;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getTimeToCompleteInSec() {
		return timeToCompleteInSec;
	}

	public void setTimeToCompleteInSec(String timeToCompleteInSec) {
		this.timeToCompleteInSec = timeToCompleteInSec;
	}

	public String getGc() {
		return gc;
	}

	public void setGc(String gc) {
		this.gc = gc;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public Integer getAnswerOptionCount() {
		return answerOptionCount;
	}

	public void setAnswerOptionCount(Integer answerOptionCount) {
		this.answerOptionCount = answerOptionCount;
	}

	public Integer getHintCount() {
		return hintCount;
	}

	public void setHintCount(Integer hintCount) {
		this.hintCount = hintCount;
	}

	public String getImportCode() {
		return importCode;
	}

	public void setImportCode(String importCode) {
		this.importCode = importCode;
	}

	public Set<String> getQuizGooruOIds() {
		return quizGooruOIds;
	}

	public void setQuizGooruOIds(Set<String> quizGooruOIds) {
		this.quizGooruOIds = quizGooruOIds;
	}

	public String getExplanationAsset() {
		return explanationAsset;
	}

	public void setExplanationAsset(String explanationAsset) {
		this.explanationAsset = explanationAsset;
	}

	public String getAnswerTexts() {
		return answerTexts;
	}

	public void setAnswerTexts(String answerTexts) {
		this.answerTexts = answerTexts;
	}

	public String getHintTexts() {
		return hintTexts;
	}

	public void setHintTexts(String hintTexts) {
		this.hintTexts = hintTexts;
	}

}