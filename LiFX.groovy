/**
 *  LiFX
 *
 *  Copyright 2014 Josh Doehla / TechUnity
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
preferences {
    input("server", "text", title: "Server", description: "Your LiFX-HTTP Server IP")
    input("port", "text", title: "Port", description: "Your LiFX-HTTP Server Port")
    input("tag", "text", title: "Tag", description: "Your LiFX Bulb group tag ('all' for all bulbs) ")
}

metadata {
	definition (name: "LiFX", namespace: "TechUnity", author: "Josh Doehla") {
		capability "Polling"
		capability "Switch Level"
		capability "Switch"
		capability "Color Control"
	}
}

simulator {
}

tiles {
	standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) 
	{
		state "on", label:'On', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
		state "off", label:'Off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
		state "turningOn", label:'Turning On', icon:"st.switches.switch.on", backgroundColor:"#79b821"
		state "turningOff", label:'Turning Off', icon:"st.switches.switch.off", backgroundColor:"#ffffff"
	}
	controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false) 
	{
		state "level", action:"switch level.setLevel"
	}
	standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat") 
	{
		state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
	}
	main "switch"
	details(["switch","refresh","levelSliderControl"])
}

def poll() {
  log.debug "Polling information from server"
  log.debug "Server: ${settings.server}:${settings.port}"
  log.debug "DeviceId: ${deviceNetworkId}"
  log.debug "Name: ${encodedName}"
  log.debug "Tag: ${tagid}"
}

def on() {
	log.debug "Turning LiFX On" 
	httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}/on?_method=put", successClosure)
  sendEvent(name: 'switch', value: "on")
}

def off() {
	log.debug "Turning LiFX Off'"
  httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}/off?_method=put", successClosure)
  sendEvent(name: 'switch', value: "off")
}

def setLevel(value) {
	log.debug "Setting level of LiFX"
  if (value == 0) {
		httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}/off?_method=put", successClosure)
    sendEvent(name: 'switch', value: "off") 
	}
  else {
    def levelDecimal = new BigDecimal(value / 100)  
		httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}/color?hue=0&saturation=0&brightness=${levelDecimal}&duration=1&_method=put", successClosure)
	}
}

def setHue(value) {
	log.debug "Setting hue of LiFX"
  def hueValue = new BigDecimal(value)
	httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}/color?hue=${hueValue}&saturation=${satDecimal}&brightness=${levelDecimal}&duration=1&_method=put", successClosure)
}

def setSaturation(value) {
	log.debug "Setting saturation of LiFX"
  def satDecimal = new BigDecimal(value / 100)
	httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}/color?hue=${hueValue}&saturation=${satDecimal}&brightness=${levelDecimal}&duration=1&_method=put", successClosure)
}

def setColor() {
	log.debug "Setting color of LiFX"
}

def successClosure = { response ->
  log.debug "Request was successful"
}

def currentOn = {
	httpGet("http://${settings.server}:${settings.port}/lights/tag:${settings.tag}", successClosure)
}
