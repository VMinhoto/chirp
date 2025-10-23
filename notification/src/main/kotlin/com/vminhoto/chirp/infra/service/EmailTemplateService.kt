package com.vminhoto.chirp.infra.service

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

/**
 * Service class that takes a template engine and has a method to process it and return the resulting html as a String
 * @property templateEngine of type TemplateEngine
 */
@Service
class EmailTemplateService(
    private val templateEngine: TemplateEngine
) {

    /**
     * Method that takes a template name as input and a map of variables. Then replace the placeholders at the template
     * according to the key value of the map.
     * @param templateName String representing the template name
     * @param variables Map with keys value representation of the information to be replaced on the template.
     */
    fun processTemplate(
        templateName: String,
        variables: Map<String, Any> = emptyMap()
    ): String {
        val context = Context().apply {
            variables.forEach { (key, value) ->
                setVariable(key, value)
            }
        }

        return templateEngine.process(templateName, context)
    }
}