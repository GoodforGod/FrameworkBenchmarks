@import io.techempower.benchmark.micronaut.model.Fortune
@import java.util.List

@param List<Fortune> fortunes
<!DOCTYPE html><html>
<head><title>Fortunes</title></head><body><table>
<tr><th>id</th><th>message</th></tr>
@for(Fortune fortune : fortunes)
    <tr><td>${fortune.getId()}</td><td>${fortune.getMessage()}</td></tr>
@endfor
</table></body></html>
