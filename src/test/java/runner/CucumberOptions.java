package runner;

public @interface CucumberOptions {

	String features();

	String glue();

	String[] plugin();

}
