<% import grails.persistence.Event %>
<%=packageName%>

<g:if test="\${!${propertyName}}">
    <elm:notFound elem="${domainClass.propertyName.capitalize()}" genero="o" />
</g:if>
<g:else>
<%  excludedProps = Event.allEvents.toList() << 'id' << 'version'
allowedNames = domainClass.persistentProperties*.name << 'dateCreated' << 'lastUpdated'
props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) }
Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
props.eachWithIndex { p, i -> %>
    <g:if test="\${${propertyName}?.${p.name}}">
        <div class="row">
            <div class="col-md-2 text-info">
                ${p.naturalName}
            </div>
            <%  if (p.isEnum()) { %>
            <div class="col-md-3">
                <g:fieldValue bean="\${${propertyName}}" field="${p.name}"/>
            </div>
            <%  } else if (p.oneToMany || p.manyToMany) { %>
            <div class="col-md-3">
                <ul>
                    <g:each in="\${${propertyName}.${p.name}}" var="${p.name[0]}">
                        <li>\${${p.name[0]}?.encodeAsHTML()}</li>
                    </g:each>
                </ul>
            </div>
            <%  } else if (p.manyToOne || p.oneToOne) { %>
            <div class="col-md-3">
                \${${propertyName}?.${p.name}?.encodeAsHTML()}
            </div>
            <%  } else if (p.type == Boolean || p.type == boolean) { %>
            <div class="col-md-3">
                <g:formatBoolean boolean="\${${propertyName}?.${p.name}}" true="Sí" false="No" />
            </div>
            <%  } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
            <div class="col-md-3">
                <g:formatDate date="\${${propertyName}?.${p.name}}" format="dd-MM-yyyy" />
            </div>
            <%  } else if(!p.type.isArray()) { %>
            <div class="col-md-3">
                <g:fieldValue bean="\${${propertyName}}" field="${p.name}"/>
            </div>
            <%  } %>
        </div>
    </g:if>
    <%  } %>
</g:else>