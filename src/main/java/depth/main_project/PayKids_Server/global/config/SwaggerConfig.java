package depth.main_project.PayKids_Server.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@SecurityScheme(
        type = SecuritySchemeType.APIKEY,
        name = "Authorization",
        description = "token 입력해주세요",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI getOpenApi() {
        return new OpenAPI()
                .components(new Components())
                .info(getInfo())
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }

    private Info getInfo() {
        return new Info()
                .title("PayKids API")
                .description("PayKids REST API DOC")
                .version("1.0.0");
    }
}
