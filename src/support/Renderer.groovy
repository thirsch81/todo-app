package support;

import groovy.text.SimpleTemplateEngine

def render(templateName, binding) {
	def engine = new SimpleTemplateEngine()
	def template = engine.createTemplate(new File("templates/${templateName}.groovy.html")).make(binding)
	template.toString()
}

def renderTestPage() {
	def ekz = [
		shoppingList: [
			items: [
				[ name: "Milch", priority : 1],
				[ name: "K&auml;se", priority: 2]
			]
		]
	]
	def content = render("ekz", ekz)
	render("main", [title: "Test", content:	content])
}