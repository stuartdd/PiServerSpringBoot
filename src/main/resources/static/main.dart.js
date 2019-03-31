// Generated by dart2js (fast startup emitter, strong), the Dart to JavaScript compiler version: 2.2.0.
// The code supports the following hooks:
// dartPrint(message):
//    if this function is defined it is called instead of the Dart [print]
//    method.
//
// dartMainRunner(main, args):
//    if this function is defined, the Dart [main] method will not be invoked
//    directly. Instead, a closure that will invoke [main], and its arguments
//    [args] is passed to [dartMainRunner].
//
// dartDeferredLibraryLoader(uri, successCallback, errorCallback):
//    if this function is defined, it will be called when a deferred library
//    is loaded. It should load and eval the javascript of `uri`, and call
//    successCallback. If it fails to do so, it should call errorCallback with
//    an error.
//
// dartCallInstrumentation(id, qualifiedName):
//    if this function is defined, it will be called at each entry of a
//    method or constructor. Used only when compiling programs with
//    --experiment-call-instrumentation.
//
// defaultPackagesBase:
//    Override the location where `package:` uris are resolved from. By default
//    they are resolved under "packages/" from the current window location.
{
}
(function dartProgram() {
  function copyProperties(from, to) {
    var keys = Object.keys(from);
    for (var i = 0; i < keys.length; i++) {
      var key = keys[i];
      to[key] = from[key];
    }
  }
  var supportsDirectProtoAccess = function() {
    var cls = function() {
    };
    cls.prototype = {p: {}};
    var object = new cls();
    if (!(object.__proto__ && object.__proto__.p === cls.prototype.p))
      return false;
    try {
      if (typeof navigator != "undefined" && typeof navigator.userAgent == "string" && navigator.userAgent.indexOf("Chrome/") >= 0)
        return true;
      if (typeof version == "function" && version.length == 0) {
        var v = version();
        if (/^\d+\.\d+\.\d+\.\d+$/.test(v))
          return true;
      }
    } catch (_) {
    }
    return false;
  }();
  function setFunctionNamesIfNecessary(holders) {
    function t() {
    }
    ;
    if (typeof t.name == "string")
      return;
    for (var i = 0; i < holders.length; i++) {
      var holder = holders[i];
      var keys = Object.keys(holder);
      for (var j = 0; j < keys.length; j++) {
        var key = keys[j];
        var f = holder[key];
        if (typeof f == 'function')
          f.name = key;
      }
    }
  }
  function inherit(cls, sup) {
    cls.prototype.constructor = cls;
    cls.prototype["$is" + cls.name] = cls;
    if (sup != null) {
      if (supportsDirectProtoAccess) {
        cls.prototype.__proto__ = sup.prototype;
        return;
      }
      var clsPrototype = Object.create(sup.prototype);
      copyProperties(cls.prototype, clsPrototype);
      cls.prototype = clsPrototype;
    }
  }
  function inheritMany(sup, classes) {
    for (var i = 0; i < classes.length; i++)
      inherit(classes[i], sup);
  }
  function mixin(cls, mixin) {
    copyProperties(mixin.prototype, cls.prototype);
    cls.prototype.constructor = cls;
  }
  function lazy(holder, name, getterName, initializer) {
    var uninitializedSentinel = holder;
    holder[name] = uninitializedSentinel;
    holder[getterName] = function() {
      holder[getterName] = function() {
        H.throwCyclicInit(name);
      };
      var result;
      var sentinelInProgress = initializer;
      try {
        if (holder[name] === uninitializedSentinel) {
          result = holder[name] = sentinelInProgress;
          result = holder[name] = initializer();
        } else
          result = holder[name];
      } finally {
        if (result === sentinelInProgress)
          holder[name] = null;
        holder[getterName] = function() {
          return this[name];
        };
      }
      return result;
    };
  }
  function makeConstList(list) {
    list.immutable$list = Array;
    list.fixed$length = Array;
    return list;
  }
  function convertToFastObject(properties) {
    function t() {
    }
    t.prototype = properties;
    new t();
    return properties;
  }
  function convertAllToFastObject(arrayOfObjects) {
    for (var i = 0; i < arrayOfObjects.length; ++i)
      convertToFastObject(arrayOfObjects[i]);
  }
  var functionCounter = 0;
  function tearOffGetter(funcs, applyTrampolineIndex, reflectionInfo, name, isIntercepted) {
    return isIntercepted ? new Function("funcs", "applyTrampolineIndex", "reflectionInfo", "name", "H", "c", "return function tearOff_" + name + functionCounter++ + "(receiver) {" + "if (c === null) c = " + "H.closureFromTearOff" + "(" + "this, funcs, applyTrampolineIndex, reflectionInfo, false, true, name);" + "return new c(this, funcs[0], receiver, name);" + "}")(funcs, applyTrampolineIndex, reflectionInfo, name, H, null) : new Function("funcs", "applyTrampolineIndex", "reflectionInfo", "name", "H", "c", "return function tearOff_" + name + functionCounter++ + "() {" + "if (c === null) c = " + "H.closureFromTearOff" + "(" + "this, funcs, applyTrampolineIndex, reflectionInfo, false, false, name);" + "return new c(this, funcs[0], null, name);" + "}")(funcs, applyTrampolineIndex, reflectionInfo, name, H, null);
  }
  function tearOff(funcs, applyTrampolineIndex, reflectionInfo, isStatic, name, isIntercepted) {
    var cache = null;
    return isStatic ? function() {
      if (cache === null)
        cache = H.closureFromTearOff(this, funcs, applyTrampolineIndex, reflectionInfo, true, false, name).prototype;
      return cache;
    } : tearOffGetter(funcs, applyTrampolineIndex, reflectionInfo, name, isIntercepted);
  }
  var typesOffset = 0;
  function installTearOff(container, getterName, isStatic, isIntercepted, requiredParameterCount, optionalParameterDefaultValues, callNames, funsOrNames, funType, applyIndex) {
    var funs = [];
    for (var i = 0; i < funsOrNames.length; i++) {
      var fun = funsOrNames[i];
      if (typeof fun == 'string')
        fun = container[fun];
      fun.$callName = callNames[i];
      funs.push(fun);
    }
    var fun = funs[0];
    fun.$requiredArgCount = requiredParameterCount;
    fun.$defaultValues = optionalParameterDefaultValues;
    var reflectionInfo = funType;
    if (typeof reflectionInfo == "number")
      reflectionInfo = reflectionInfo + typesOffset;
    var name = funsOrNames[0];
    fun.$stubName = name;
    var getterFunction = tearOff(funs, applyIndex || 0, reflectionInfo, isStatic, name, isIntercepted);
    container[getterName] = getterFunction;
    if (isStatic)
      fun.$tearOff = getterFunction;
  }
  function installStaticTearOff(container, getterName, requiredParameterCount, optionalParameterDefaultValues, callNames, funsOrNames, funType, applyIndex) {
    return installTearOff(container, getterName, true, false, requiredParameterCount, optionalParameterDefaultValues, callNames, funsOrNames, funType, applyIndex);
  }
  function installInstanceTearOff(container, getterName, isIntercepted, requiredParameterCount, optionalParameterDefaultValues, callNames, funsOrNames, funType, applyIndex) {
    return installTearOff(container, getterName, false, isIntercepted, requiredParameterCount, optionalParameterDefaultValues, callNames, funsOrNames, funType, applyIndex);
  }
  function setOrUpdateInterceptorsByTag(newTags) {
    var tags = init.interceptorsByTag;
    if (!tags) {
      init.interceptorsByTag = newTags;
      return;
    }
    copyProperties(newTags, tags);
  }
  function setOrUpdateLeafTags(newTags) {
    var tags = init.leafTags;
    if (!tags) {
      init.leafTags = newTags;
      return;
    }
    copyProperties(newTags, tags);
  }
  function updateTypes(newTypes) {
    var types = init.types;
    var length = types.length;
    types.push.apply(types, newTypes);
    return length;
  }
  function updateHolder(holder, newHolder) {
    copyProperties(newHolder, holder);
    return holder;
  }
  var hunkHelpers = function() {
    var mkInstance = function(isIntercepted, requiredParameterCount, optionalParameterDefaultValues, callNames, applyIndex) {
        return function(container, getterName, name, funType) {
          return installInstanceTearOff(container, getterName, isIntercepted, requiredParameterCount, optionalParameterDefaultValues, callNames, [name], funType, applyIndex);
        };
      },
      mkStatic = function(requiredParameterCount, optionalParameterDefaultValues, callNames, applyIndex) {
        return function(container, getterName, name, funType) {
          return installStaticTearOff(container, getterName, requiredParameterCount, optionalParameterDefaultValues, callNames, [name], funType, applyIndex);
        };
      };
    return {inherit: inherit, inheritMany: inheritMany, mixin: mixin, installStaticTearOff: installStaticTearOff, installInstanceTearOff: installInstanceTearOff, _instance_0u: mkInstance(0, 0, null, ["call$0"], 0), _instance_1u: mkInstance(0, 1, null, ["call$1"], 0), _instance_2u: mkInstance(0, 2, null, ["call$2"], 0), _instance_0i: mkInstance(1, 0, null, ["call$0"], 0), _instance_1i: mkInstance(1, 1, null, ["call$1"], 0), _instance_2i: mkInstance(1, 2, null, ["call$2"], 0), _static_0: mkStatic(0, null, ["call$0"], 0), _static_1: mkStatic(1, null, ["call$1"], 0), _static_2: mkStatic(2, null, ["call$2"], 0), makeConstList: makeConstList, lazy: lazy, updateHolder: updateHolder, convertToFastObject: convertToFastObject, setFunctionNamesIfNecessary: setFunctionNamesIfNecessary, updateTypes: updateTypes, setOrUpdateInterceptorsByTag: setOrUpdateInterceptorsByTag, setOrUpdateLeafTags: setOrUpdateLeafTags};
  }();
  function initializeDeferredHunk(hunk) {
    typesOffset = init.types.length;
    hunk(hunkHelpers, init, holders, $);
  }
  function getGlobalFromName(name) {
    for (var i = 0; i < holders.length; i++) {
      if (holders[i] == null)
        continue;
      if (holders[i][name])
        return holders[i][name];
    }
  }
  var J = {Interceptor: function Interceptor() {
    }},
  P = {Object: function Object() {
    }},
  W = {},
  F = {
    main: function() {
      document.querySelector("#output").textContent = "Your Dart app is running.";
    }
  };
  var holders = [J, P, W, F];
  hunkHelpers.setFunctionNamesIfNecessary(holders);
  var $ = {};
  J.Interceptor.prototype = {};
  P.Object.prototype = {constructor: P.Object, $isObject: 1,
    toString: function() {
      return this.toString$0(this);
    }
  };
  (function inheritance() {
    var _inherit = hunkHelpers.inherit;
    _inherit(P.Object, null);
    _inherit(J.Interceptor, P.Object);
  })();
  var init = {mangledGlobalNames: {int: "int", double: "double", num: "num", String: "String", bool: "bool", Null: "Null", List: "List"}, mangledNames: {}, getTypeFromName: getGlobalFromName, metadata: [], types: [], interceptorsByTag: null, leafTags: null};
  (function nativeSupport() {
    !function() {
      var intern = function(s) {
        var o = {};
        o[s] = 1;
        return Object.keys(hunkHelpers.convertToFastObject(o))[0];
      };
      init.getIsolateTag = function(name) {
        return intern("___dart_" + name + init.isolateTag);
      };
      var tableProperty = "___dart_isolate_tags_";
      var usedProperties = Object[tableProperty] || (Object[tableProperty] = Object.create(null));
      var rootProperty = "_ZxYxX";
      for (var i = 0;; i++) {
        var property = intern(rootProperty + "_" + i + "_");
        if (!(property in usedProperties)) {
          usedProperties[property] = 1;
          init.isolateTag = property;
          break;
        }
      }
    }();
    hunkHelpers.setOrUpdateInterceptorsByTag({HTMLElement: J.Interceptor, HTMLAnchorElement: J.Interceptor, ApplicationCacheErrorEvent: J.Interceptor, HTMLAreaElement: J.Interceptor, HTMLAudioElement: J.Interceptor, HTMLBRElement: J.Interceptor, HTMLBaseElement: J.Interceptor, HTMLBodyElement: J.Interceptor, HTMLButtonElement: J.Interceptor, HTMLCanvasElement: J.Interceptor, HTMLContentElement: J.Interceptor, HTMLDListElement: J.Interceptor, HTMLDataElement: J.Interceptor, HTMLDataListElement: J.Interceptor, HTMLDetailsElement: J.Interceptor, HTMLDialogElement: J.Interceptor, HTMLDivElement: J.Interceptor, Document: J.Interceptor, DOMError: J.Interceptor, DOMException: J.Interceptor, Element: J.Interceptor, HTMLEmbedElement: J.Interceptor, ErrorEvent: J.Interceptor, Event: J.Interceptor, InputEvent: J.Interceptor, EventTarget: J.Interceptor, HTMLFieldSetElement: J.Interceptor, HTMLFormElement: J.Interceptor, HTMLHRElement: J.Interceptor, HTMLHeadElement: J.Interceptor, HTMLHeadingElement: J.Interceptor, HTMLDocument: J.Interceptor, HTMLHtmlElement: J.Interceptor, HTMLIFrameElement: J.Interceptor, HTMLImageElement: J.Interceptor, HTMLInputElement: J.Interceptor, HTMLLIElement: J.Interceptor, HTMLLabelElement: J.Interceptor, HTMLLegendElement: J.Interceptor, HTMLLinkElement: J.Interceptor, HTMLMapElement: J.Interceptor, HTMLMediaElement: J.Interceptor, MediaError: J.Interceptor, HTMLMenuElement: J.Interceptor, HTMLMetaElement: J.Interceptor, HTMLMeterElement: J.Interceptor, HTMLModElement: J.Interceptor, NavigatorUserMediaError: J.Interceptor, Node: J.Interceptor, HTMLOListElement: J.Interceptor, HTMLObjectElement: J.Interceptor, HTMLOptGroupElement: J.Interceptor, HTMLOptionElement: J.Interceptor, HTMLOutputElement: J.Interceptor, OverconstrainedError: J.Interceptor, HTMLParagraphElement: J.Interceptor, HTMLParamElement: J.Interceptor, HTMLPictureElement: J.Interceptor, PositionError: J.Interceptor, HTMLPreElement: J.Interceptor, HTMLProgressElement: J.Interceptor, HTMLQuoteElement: J.Interceptor, HTMLScriptElement: J.Interceptor, HTMLSelectElement: J.Interceptor, SensorErrorEvent: J.Interceptor, HTMLShadowElement: J.Interceptor, HTMLSlotElement: J.Interceptor, HTMLSourceElement: J.Interceptor, HTMLSpanElement: J.Interceptor, SpeechRecognitionError: J.Interceptor, HTMLStyleElement: J.Interceptor, HTMLTableCaptionElement: J.Interceptor, HTMLTableCellElement: J.Interceptor, HTMLTableDataCellElement: J.Interceptor, HTMLTableHeaderCellElement: J.Interceptor, HTMLTableColElement: J.Interceptor, HTMLTableElement: J.Interceptor, HTMLTableRowElement: J.Interceptor, HTMLTableSectionElement: J.Interceptor, HTMLTemplateElement: J.Interceptor, HTMLTextAreaElement: J.Interceptor, HTMLTimeElement: J.Interceptor, HTMLTitleElement: J.Interceptor, HTMLTrackElement: J.Interceptor, HTMLUListElement: J.Interceptor, HTMLUnknownElement: J.Interceptor, HTMLVideoElement: J.Interceptor, HTMLDirectoryElement: J.Interceptor, HTMLFontElement: J.Interceptor, HTMLFrameElement: J.Interceptor, HTMLFrameSetElement: J.Interceptor, HTMLMarqueeElement: J.Interceptor, SVGAElement: J.Interceptor, SVGAnimateElement: J.Interceptor, SVGAnimateMotionElement: J.Interceptor, SVGAnimateTransformElement: J.Interceptor, SVGAnimationElement: J.Interceptor, SVGCircleElement: J.Interceptor, SVGClipPathElement: J.Interceptor, SVGDefsElement: J.Interceptor, SVGDescElement: J.Interceptor, SVGDiscardElement: J.Interceptor, SVGEllipseElement: J.Interceptor, SVGFEBlendElement: J.Interceptor, SVGFEColorMatrixElement: J.Interceptor, SVGFEComponentTransferElement: J.Interceptor, SVGFECompositeElement: J.Interceptor, SVGFEConvolveMatrixElement: J.Interceptor, SVGFEDiffuseLightingElement: J.Interceptor, SVGFEDisplacementMapElement: J.Interceptor, SVGFEDistantLightElement: J.Interceptor, SVGFEFloodElement: J.Interceptor, SVGFEFuncAElement: J.Interceptor, SVGFEFuncBElement: J.Interceptor, SVGFEFuncGElement: J.Interceptor, SVGFEFuncRElement: J.Interceptor, SVGFEGaussianBlurElement: J.Interceptor, SVGFEImageElement: J.Interceptor, SVGFEMergeElement: J.Interceptor, SVGFEMergeNodeElement: J.Interceptor, SVGFEMorphologyElement: J.Interceptor, SVGFEOffsetElement: J.Interceptor, SVGFEPointLightElement: J.Interceptor, SVGFESpecularLightingElement: J.Interceptor, SVGFESpotLightElement: J.Interceptor, SVGFETileElement: J.Interceptor, SVGFETurbulenceElement: J.Interceptor, SVGFilterElement: J.Interceptor, SVGForeignObjectElement: J.Interceptor, SVGGElement: J.Interceptor, SVGGeometryElement: J.Interceptor, SVGGraphicsElement: J.Interceptor, SVGImageElement: J.Interceptor, SVGLineElement: J.Interceptor, SVGLinearGradientElement: J.Interceptor, SVGMarkerElement: J.Interceptor, SVGMaskElement: J.Interceptor, SVGMetadataElement: J.Interceptor, SVGPathElement: J.Interceptor, SVGPatternElement: J.Interceptor, SVGPolygonElement: J.Interceptor, SVGPolylineElement: J.Interceptor, SVGRadialGradientElement: J.Interceptor, SVGRectElement: J.Interceptor, SVGScriptElement: J.Interceptor, SVGSetElement: J.Interceptor, SVGStopElement: J.Interceptor, SVGStyleElement: J.Interceptor, SVGElement: J.Interceptor, SVGSVGElement: J.Interceptor, SVGSwitchElement: J.Interceptor, SVGSymbolElement: J.Interceptor, SVGTSpanElement: J.Interceptor, SVGTextContentElement: J.Interceptor, SVGTextElement: J.Interceptor, SVGTextPathElement: J.Interceptor, SVGTextPositioningElement: J.Interceptor, SVGTitleElement: J.Interceptor, SVGUseElement: J.Interceptor, SVGViewElement: J.Interceptor, SVGGradientElement: J.Interceptor, SVGComponentTransferFunctionElement: J.Interceptor, SVGFEDropShadowElement: J.Interceptor, SVGMPathElement: J.Interceptor, SQLError: J.Interceptor});
    hunkHelpers.setOrUpdateLeafTags({HTMLElement: true, HTMLAnchorElement: true, ApplicationCacheErrorEvent: true, HTMLAreaElement: true, HTMLAudioElement: true, HTMLBRElement: true, HTMLBaseElement: true, HTMLBodyElement: true, HTMLButtonElement: true, HTMLCanvasElement: true, HTMLContentElement: true, HTMLDListElement: true, HTMLDataElement: true, HTMLDataListElement: true, HTMLDetailsElement: true, HTMLDialogElement: true, HTMLDivElement: true, Document: true, DOMError: true, DOMException: true, Element: true, HTMLEmbedElement: true, ErrorEvent: true, Event: true, InputEvent: true, EventTarget: true, HTMLFieldSetElement: true, HTMLFormElement: true, HTMLHRElement: true, HTMLHeadElement: true, HTMLHeadingElement: true, HTMLDocument: true, HTMLHtmlElement: true, HTMLIFrameElement: true, HTMLImageElement: true, HTMLInputElement: true, HTMLLIElement: true, HTMLLabelElement: true, HTMLLegendElement: true, HTMLLinkElement: true, HTMLMapElement: true, HTMLMediaElement: true, MediaError: true, HTMLMenuElement: true, HTMLMetaElement: true, HTMLMeterElement: true, HTMLModElement: true, NavigatorUserMediaError: true, Node: true, HTMLOListElement: true, HTMLObjectElement: true, HTMLOptGroupElement: true, HTMLOptionElement: true, HTMLOutputElement: true, OverconstrainedError: true, HTMLParagraphElement: true, HTMLParamElement: true, HTMLPictureElement: true, PositionError: true, HTMLPreElement: true, HTMLProgressElement: true, HTMLQuoteElement: true, HTMLScriptElement: true, HTMLSelectElement: true, SensorErrorEvent: true, HTMLShadowElement: true, HTMLSlotElement: true, HTMLSourceElement: true, HTMLSpanElement: true, SpeechRecognitionError: true, HTMLStyleElement: true, HTMLTableCaptionElement: true, HTMLTableCellElement: true, HTMLTableDataCellElement: true, HTMLTableHeaderCellElement: true, HTMLTableColElement: true, HTMLTableElement: true, HTMLTableRowElement: true, HTMLTableSectionElement: true, HTMLTemplateElement: true, HTMLTextAreaElement: true, HTMLTimeElement: true, HTMLTitleElement: true, HTMLTrackElement: true, HTMLUListElement: true, HTMLUnknownElement: true, HTMLVideoElement: true, HTMLDirectoryElement: true, HTMLFontElement: true, HTMLFrameElement: true, HTMLFrameSetElement: true, HTMLMarqueeElement: true, SVGAElement: true, SVGAnimateElement: true, SVGAnimateMotionElement: true, SVGAnimateTransformElement: true, SVGAnimationElement: true, SVGCircleElement: true, SVGClipPathElement: true, SVGDefsElement: true, SVGDescElement: true, SVGDiscardElement: true, SVGEllipseElement: true, SVGFEBlendElement: true, SVGFEColorMatrixElement: true, SVGFEComponentTransferElement: true, SVGFECompositeElement: true, SVGFEConvolveMatrixElement: true, SVGFEDiffuseLightingElement: true, SVGFEDisplacementMapElement: true, SVGFEDistantLightElement: true, SVGFEFloodElement: true, SVGFEFuncAElement: true, SVGFEFuncBElement: true, SVGFEFuncGElement: true, SVGFEFuncRElement: true, SVGFEGaussianBlurElement: true, SVGFEImageElement: true, SVGFEMergeElement: true, SVGFEMergeNodeElement: true, SVGFEMorphologyElement: true, SVGFEOffsetElement: true, SVGFEPointLightElement: true, SVGFESpecularLightingElement: true, SVGFESpotLightElement: true, SVGFETileElement: true, SVGFETurbulenceElement: true, SVGFilterElement: true, SVGForeignObjectElement: true, SVGGElement: true, SVGGeometryElement: true, SVGGraphicsElement: true, SVGImageElement: true, SVGLineElement: true, SVGLinearGradientElement: true, SVGMarkerElement: true, SVGMaskElement: true, SVGMetadataElement: true, SVGPathElement: true, SVGPatternElement: true, SVGPolygonElement: true, SVGPolylineElement: true, SVGRadialGradientElement: true, SVGRectElement: true, SVGScriptElement: true, SVGSetElement: true, SVGStopElement: true, SVGStyleElement: true, SVGElement: true, SVGSVGElement: true, SVGSwitchElement: true, SVGSymbolElement: true, SVGTSpanElement: true, SVGTextContentElement: true, SVGTextElement: true, SVGTextPathElement: true, SVGTextPositioningElement: true, SVGTitleElement: true, SVGUseElement: true, SVGViewElement: true, SVGGradientElement: true, SVGComponentTransferFunctionElement: true, SVGFEDropShadowElement: true, SVGMPathElement: true, SQLError: true});
  })();
  convertAllToFastObject(holders);
  convertToFastObject($);
  (function(callback) {
    if (typeof document === "undefined") {
      callback(null);
      return;
    }
    if (typeof document.currentScript != 'undefined') {
      callback(document.currentScript);
      return;
    }
    var scripts = document.scripts;
    function onLoad(event) {
      for (var i = 0; i < scripts.length; ++i)
        scripts[i].removeEventListener("load", onLoad, false);
      callback(event.target);
    }
    for (var i = 0; i < scripts.length; ++i)
      scripts[i].addEventListener("load", onLoad, false);
  })(function(currentScript) {
    init.currentScript = currentScript;
    if (typeof dartMainRunner === "function")
      dartMainRunner(F.main, []);
    else
      F.main([]);
  });
})();

//# sourceMappingURL=main.dart.js.map
