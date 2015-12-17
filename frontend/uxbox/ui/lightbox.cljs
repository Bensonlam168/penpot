(ns uxbox.ui.lightbox
  (:require [sablono.core :as html :refer-macros [html]]
            [rum.core :as rum]
            [uxbox.ui.util :as util]
            [uxbox.ui.keyboard :as k]
            [goog.events :as events])
  (:import goog.events.EventType))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; State Management
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce +current+ (atom nil))

(defn set!
  [kind]
  (reset! +current+ kind))

(defn close!
  []
  (reset! +current+ nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UI
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti render-lightbox identity)
(defmethod render-lightbox :default [_] nil)

(defn- on-esc-clicked
  [e]
  (when (k/esc? e)
    (close!)))

(defn- lightbox-will-mount
  [state]
  (events/listen js/document
                 EventType.KEYDOWN
                 on-esc-clicked)
  state)

(defn- lightbox-will-umount
  [state]
  (events/unlisten js/document
                   EventType.KEYDOWN
                   on-esc-clicked)
  state)

(defn- lightbox-render
  [own]
  (let [name (rum/react +current+)]
    (html
     [:div.lightbox {:class (when (nil? name) "hide")}
      (render-lightbox name)])))

(def ^:static lightbox
  (util/component
   {:name "lightbox"
    :render lightbox-render
    :will-mount lightbox-will-mount
    :will-unmount lightbox-will-umount
    :mixins [rum/reactive]}))
