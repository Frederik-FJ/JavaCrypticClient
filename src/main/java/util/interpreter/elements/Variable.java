package util.interpreter.elements;

public class Variable{

    String name;
    Object content;

    public Variable(String name, Object content){
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", content=" + content;
    }
}
