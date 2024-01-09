Controller classes are not a good design pattern when it comes to swing ui development.

All they create is a second class with way too large of a dependency to the view class; you might as well just do whatever you need to do in the view class.

Anything in this package should be considered legacy and is marked for removal. Please do not imitate this pattern with other views.