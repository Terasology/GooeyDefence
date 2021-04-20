# Parsers

A parser is any class that extends the BaseParser abstract class.
It contains information about the component it applies to, the fields that should be displayed and methods to convert those field values into human-readable forms.

Firstly, a parser is only applied onto the class that is returned by the `getComponentClass()` method.
As not all fields on a component should be displayed in UI, the `getFields()` method returns a map between a field name, and the display name.
If a field is not present as a key in this map, then it is not displayed
The fields are also displayed in alphabetical order, according to the field name, not the display name.
This is to allow field names, values and upgrade values to all line up.

Lastly, and this is where the reflection magic happens, the parser should contain methods to convert field values to readable values.
These methods should have a specific structure:
* The return type should be string
* The name of the method must be the same as the name of the field - capitalisation matters
* The first parameter should be of type boolean - it will indicate if the value is actually an upgrade values
* The second parameter should have the same type as the field - this will be passed the value to convert

If a method matching this is not found, then either the `handleField()` or `handleUpgrade()` methods are called.
These are backup methods that take a string field name and an object value.
By default, `handleField()` simply calls `String.valueOf()` on the value passed, and `handleUpgrade()` calls `handleField()` and prepends a '+' to the result.

As of present the value of a field being converted can be an Enum, String, or any of the six primitive numbers.
Other values may be supported in the future.
Alternatively, an object default might be added.

Of note here is that both component field values and upgrade values are passed through the parser.
The boolean flag, or differing handle methods allow for different conversion of upgrades vs field values.

