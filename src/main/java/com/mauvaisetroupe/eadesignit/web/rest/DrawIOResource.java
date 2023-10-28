package com.mauvaisetroupe.eadesignit.web.rest;

import com.mauvaisetroupe.eadesignit.domain.LandscapeView;
import com.mauvaisetroupe.eadesignit.repository.LandscapeViewRepository;
import com.mauvaisetroupe.eadesignit.service.diagram.drawio.MXFileSerializer;
import com.mauvaisetroupe.eadesignit.service.diagram.plantuml.PlantUMLBuilder.Layout;
import com.mauvaisetroupe.eadesignit.service.diagram.plantuml.PlantUMLService;
import io.undertow.util.BadRequestException;
import java.io.IOException;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/api")
@Transactional
public class DrawIOResource {

    private final LandscapeViewRepository landscapeViewRepository;
    private final PlantUMLService plantUMLSerializer;

    private final Logger log = LoggerFactory.getLogger(DrawIOResource.class);

    public DrawIOResource(LandscapeViewRepository landscapeViewRepository, PlantUMLService plantUMLSerializer) {
        this.landscapeViewRepository = landscapeViewRepository;
        this.plantUMLSerializer = plantUMLSerializer;
    }

    @GetMapping(value = "drawio/landscape-view/get-xml/{id}")
    public @ResponseBody String getLandscapeXML(@PathVariable("id") Long id)
        throws IOException, BadRequestException, ParserConfigurationException, XPathExpressionException, SAXException {
        log.debug("REST request to get LandscapeView : {}", id);
        LandscapeView landscape = landscapeViewRepository.findById(id).orElseThrow();
        MXFileSerializer fileSerializer = new MXFileSerializer(landscape);
        if (StringUtils.hasText(landscape.getCompressedDrawXML())) {
            // If no draw.io XML is persisted, create one in order to have a draft to edit
            String newXML = fileSerializer.updateMXFileXML();
            if (newXML != null) {
                landscape.setCompressedDrawSVG(null);
                landscape.setCompressedDrawXML(newXML);
            }
            return landscape.getCompressedDrawXML();
        } else {
            // check if drawio is uptodate, if not remove SVG from database
            // and send updated xml
            String svgXML = plantUMLSerializer.getLandscapeDiagramSVG(landscape, Layout.elk, false, true, true);
            //svg needed only for application positioner
            return fileSerializer.createMXFileXML(svgXML);
        }
    }
}
