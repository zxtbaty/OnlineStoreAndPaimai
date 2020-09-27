module.exports = {
	publicPath: './',
    devServer: {
        proxy: {
            "/wx": {
                target:"http://localhost:8082/",
                // target: "http://101.37.178.212:8000",
                changeOrigin: true
            }
        }
    }
}