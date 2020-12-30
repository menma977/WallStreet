package info.wallstreet.model

object Url {
  /**
   * @param target String
   * @return String
   */
  fun web(target: String): String {
    return "https://bugnode.info/api/${target.replace(".", "/")}"
  }

  /**
   * @return String
   */
  fun doge(): String {
    return "https://www.999doge.com/api/web.aspx"
  }

  /**
   * @return String
   */
  fun camel(endpoint: String): String {
    var target = endpoint
    if (endpoint.startsWith("/")) target = target.substring(1)
    return "https://api.cameltoken.io/tronapi/$target"
  }

  /**
   * @return String
   */
  fun keyDoge(): String {
    return "ec01af0702f3467a808ba52679e1ee61"
  }
}