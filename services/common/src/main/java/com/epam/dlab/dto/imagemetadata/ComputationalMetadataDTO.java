package com.epam.dlab.dto.imagemetadata;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Viktor Chukhra <Viktor_Chukhra@epam.com>
 */
public class ComputationalMetadataDTO extends ImageMetadataDTO{
    @JsonProperty(value = "template_name")
    private String templateName;
    @JsonProperty
    private String description;
    @JsonProperty("environment_type")
    private String type;
    @JsonProperty
    private List<TemplateDTO> templates;
    @JsonProperty(value = "request_id")
    private String requestId;
    @JsonProperty(value = "computation_resources_shapes")
    private List<ComputationalResourceShapeDto> computationResourceShapes;
    @JsonProperty
    protected String image;

    public ComputationalMetadataDTO(String imageName) {
        this.image = imageName;
        setImageType(ImageType.COMPUTATIONAL);
    }

    public ComputationalMetadataDTO() {
        this("");
    }


    public ComputationalMetadataDTO(String image, String templateName, String description, String requestId,
                                    String type,
                                    List<TemplateDTO> templates) {
        this.image = image;
        this.templateName = templateName;
        this.description = description;
        this.requestId = requestId;
        this.type = type;
        this.templates = templates;
        setImageType(ImageType.COMPUTATIONAL);
    }


    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TemplateDTO> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateDTO> templates) {
        this.templates = templates;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<ComputationalResourceShapeDto> getComputationResourceShapes() {
        return computationResourceShapes;
    }

    public void setComputationResourceShapes(List<ComputationalResourceShapeDto> computationResourceShapes) {
        this.computationResourceShapes = computationResourceShapes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComputationalMetadataDTO that = (ComputationalMetadataDTO) o;

        if (templateName != null ? !templateName.equals(that.templateName)
                : that.templateName != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description)
                : that.description != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (templates != null ? !templates.equals(that.templates)
                : that.templates != null) {
            return false;
        }
        if (requestId != null ? !requestId.equals(that.requestId)
                : that.requestId != null) {
            return false;
        }
        if (computationResourceShapes != null ? !computationResourceShapes
                .equals(that.computationResourceShapes)
                : that.computationResourceShapes != null) {
            return false;
        }
        return image != null ? image.equals(that.image) : that.image == null;
    }

    @Override
    public int hashCode() {
        int result = templateName != null ? templateName.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode()
                : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (templates != null ? templates.hashCode() : 0);
        result = 31 * result + (requestId != null ? requestId.hashCode() : 0);
        result = 31 * result + (computationResourceShapes != null
                ? computationResourceShapes.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }
}
