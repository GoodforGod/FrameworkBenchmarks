@import io.micronaut.benchmark.jpa.model.Fortune
@import java.util.List

@param io.micronaut.benchmark.jpa.controller.FortunesModel model
<!DOCTYPE html><html>
<head><title>Fortunes</title></head><body><table>
<tr><th>id</th><th>message</th></tr>
@for(Fortune fortune : model.getFortunes())
    <tr><td>${fortune.id}</td><td>${fortune.message}</td></tr>
@endfor
</table></body></html>
