<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<video <c:if test="${requestScope.video.width ne null}">
		width="${requestScope.video.width}"
	</c:if>
	<c:if test="${requestScope.video.height ne null}">
		height="${requestScope.video.height}"
	</c:if>
	<c:if test="${requestScope.video.poster ne null}">
		poster="${requestScope.video.poster}"
	</c:if>
	<c:if test="${requestScope.video.loop}">
		loop
	</c:if>
	<c:if test="${requestScope.video.muted}">
		muted
	</c:if>
	<c:if test="${requestScope.video.autoplay}">
		autoplay
	</c:if>
	<c:if test="${requestScope.video.preload}">
		preload
	</c:if>
	<c:if test="${requestScope.video.src ne null}">
		src="${requestScope.video.src}?ticket=${requestScope.video.ticket}"
	</c:if>
	<c:if test="${requestScope.video.controls}">
		controls
	</c:if>
/>