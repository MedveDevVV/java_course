package autoservice.dto;

public record JwtResponse(
        String token,
        String userName
) {
    public String type() {
        return "Bearer";
    }
}
