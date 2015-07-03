# PlayJPA
Play 1 JPA Model for Play 2 

Jave developer who familiar with JPA Model (Play 1) will like PlayJPA very much.
Please refer [Play!](https://www.playframework.com/documentation/1.3.x/jpa#anamefindingFindingobjectsa) for more detail about the Model. Unfortunely [Explicit Save](https://www.playframework.com/documentation/1.3.x/jpa#anamesaveExplicitsavea) is not implemented as i am still thinking is it a good practice of hacking the normal JPA behaviour.

## Installation
Add dependency declarations into your build.sbt file:
```
"com.fliptoo" %% "playjpa" % "1.0-SNAPSHOT"
```
Enable PlayJPA in application.conf
```
play.modules.enabled += "com.fliptoo.play.jpa.Module"
```
## Quick Start

Extend your JPA entity with the Model class

```
@Entity
public class User extends Model {

    @Id
    @GeneratedValue
    public Long id;

    public String name;

}
```

Do whatever as you did as Play 1
```
public class Application extends Controller {

    @Transactional
    public Result index() {
        User user = User.find("byName", "fliptoo").first();
        return ok(index.render("I am " + user.name));
    }

}
```

## License

(The MIT License)

Copyright (c) 2015 Fliptoo &lt;fliptoo.studio@gmail.com&gt;

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
