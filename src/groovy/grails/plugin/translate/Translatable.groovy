package grails.plugin.translate

interface Translatable {

    final static int MAX_TEXT_SIZE = 5000

    final static constraints = {
        original nullable: false, blank: false, maxSize: MAX_TEXT_SIZE
        from     nullable: true,  validator: { value, target -> if(target.to == value) { ['same.locale'] } }
        to       nullable: false
    }

    String getOriginal()    
    void setResult(String result)

    Locale getFrom()
    Locale getTo()

}
