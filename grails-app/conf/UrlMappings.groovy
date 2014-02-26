class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.${format})?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "inicio", action: "index")

        "401"(controller: 'shield', action: 'unauthorized')
        "404"(controller: 'shield', action: 'notFound')
        "403"(controller: 'shield', action: 'forbidden')
        "500"(controller: 'shield', action: 'internalServerError')
    }
}
